package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

data class UserProfile(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val workspaceRole: String = "Lead DevOps Engineer",
    val workspaceOrg: String = "Acme Engineering"
)

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: UserProfile) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthManager(private val context: Context) {

    private val auth: FirebaseAuth? = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        Log.w("AuthManager", "FirebaseAuth not initialized from google-services.json: ${e.message}")
        null
    }

    private val credentialManager = CredentialManager.create(context)

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    init {
        // Check if Firebase user already cached
        auth?.currentUser?.let { fbUser ->
            val profile = UserProfile(
                uid = fbUser.uid,
                displayName = fbUser.displayName ?: "Workspace Engineer",
                email = fbUser.email ?: "developer@syncflow.io",
                photoUrl = fbUser.photoUrl?.toString()
            )
            _currentUser.value = profile
            _authUiState.value = AuthUiState.Success(profile)
        }
    }

    suspend fun signInWithGoogle(): Boolean = withContext(Dispatchers.Main) {
        _authUiState.value = AuthUiState.Loading

        val webClientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (_: Exception) {
            "dummy-web-client-id.apps.googleusercontent.com"
        }

        try {
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                // Sign in with Firebase if FirebaseAuth is available
                if (auth != null) {
                    val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val fbUser = kotlinx.coroutines.suspendCancellableCoroutine<com.google.firebase.auth.FirebaseUser?> { cont ->
                        auth.signInWithCredential(authCredential)
                            .addOnSuccessListener { res ->
                                cont.resume(res.user)
                            }
                            .addOnFailureListener { ex ->
                                cont.resumeWith(Result.failure(ex))
                            }
                    }
                    val profile = UserProfile(
                        uid = fbUser?.uid ?: "user_google_${System.currentTimeMillis()}",
                        displayName = googleIdTokenCredential.displayName ?: fbUser?.displayName ?: "Google Workspace User",
                        email = googleIdTokenCredential.id,
                        photoUrl = googleIdTokenCredential.profilePictureUri?.toString() ?: fbUser?.photoUrl?.toString()
                    )
                    _currentUser.value = profile
                    _authUiState.value = AuthUiState.Success(profile)
                    return@withContext true
                } else {
                    val profile = UserProfile(
                        uid = "usr_g_${System.currentTimeMillis()}",
                        displayName = googleIdTokenCredential.displayName ?: "Google Workspace User",
                        email = googleIdTokenCredential.id,
                        photoUrl = googleIdTokenCredential.profilePictureUri?.toString()
                    )
                    _currentUser.value = profile
                    _authUiState.value = AuthUiState.Success(profile)
                    return@withContext true
                }
            } else {
                _authUiState.value = AuthUiState.Error("Unexpected credential type received.")
                return@withContext false
            }
        } catch (e: GetCredentialException) {
            Log.e("AuthManager", "Google Sign-In failed or cancelled: ${e.message}")
            _authUiState.value = AuthUiState.Error("Google Sign-In: ${e.message ?: "Authentication cancelled or unavailable."}")
            return@withContext false
        } catch (e: Exception) {
            Log.e("AuthManager", "Authentication error: ${e.message}", e)
            _authUiState.value = AuthUiState.Error("Sign-In failed: ${e.message}")
            return@withContext false
        }
    }

    fun signInAsDevAdmin(email: String = "karantagde2@gmail.com", name: String = "Karan Tagde") {
        val profile = UserProfile(
            uid = "usr_dev_admin_712",
            displayName = name,
            email = email,
            workspaceRole = "Principal Automation Architect",
            workspaceOrg = "Acme Engineering / SyncFlow Core"
        )
        _currentUser.value = profile
        _authUiState.value = AuthUiState.Success(profile)
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.w("AuthManager", "Error during Firebase signOut", e)
        }
        _currentUser.value = null
        _authUiState.value = AuthUiState.Idle
    }
}
