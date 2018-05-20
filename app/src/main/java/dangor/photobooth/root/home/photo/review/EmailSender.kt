package dangor.photobooth.root.home.photo.review

import android.accounts.AccountManager
import android.content.Context
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.Base64
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.Message
import dangor.photobooth.services.PermissionService
import dangor.photobooth.services.permissions.Permission
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Properties
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class EmailSender(appContext: Context, private val permissionService: PermissionService) {

    private val credential = GoogleAccountCredential.usingOAuth2(appContext, SCOPES)
            .setBackOff(ExponentialBackOff())
    private val sharedPref = appContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)

    fun sendPhoto(to: String, file: File): Observable<Unit> {
        val content = createEmailWithAttachment(to, SUBJECT, BODY, file)
        try {
            sendMessage(getService(), "me", content)
        } catch (e: UserRecoverableAuthIOException) {
            return permissionService.startActivityForResult(e.intent, RC_AUTHZ)
                    .observeOn(Schedulers.io())
                    .switchMap { sendPhoto(to, file) }
        } catch (e: Exception) {
            throw SendEmailException(to, e)
        }
        return Observable.just(Unit)
    }

    fun initialize(): Single<Unit> {
        return permissionService.request(Permission.GET_ACCOUNTS)
                .switchMap {
                    val accountName = sharedPref.getString(PREF_ACCOUNT_NAME, null)
                    Log.i(TAG, "account name from shared pref: $accountName")
                    if (accountName != null) {
                        credential.selectedAccountName = accountName
                        Observable.just(Unit)
                    } else {
                        chooseAccount()
                    }
                }
                .firstOrError()
    }

    private fun getService(): Gmail {
        return Gmail.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        )
                .setApplicationName("Dang Photobooth")
                .build()
    }

    private fun chooseAccount(): Observable<Unit> {
        return permissionService.startActivityForResult(credential.newChooseAccountIntent(), RC_ACCOUNT_CHOOSER)
                .doOnNext {
                    val accountName = it.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                    Log.i(TAG, "account name from intent: $accountName")
                    credential.selectedAccountName = accountName
                    val editor = sharedPref.edit()
                    editor.putString(PREF_ACCOUNT_NAME, accountName)
                    editor.apply()
                }
                .map { Unit }
    }

    private fun createEmailWithAttachment(to: String,
                                          subject: String,
                                          bodyText: String,
                                          file: File): MimeMessage {
        val props = Properties()
        val session = Session.getDefaultInstance(props, null)

        val email = MimeMessage(session)
        email.setFrom(InternetAddress(credential.selectedAccountName, FROM_PERSONAL))
        email.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(to))
        email.subject = subject

        val multipart = MimeMultipart()

        val mimeText = MimeBodyPart()
        mimeText.setContent(bodyText, "text/plain")
        multipart.addBodyPart(mimeText)

        val mimeFile = MimeBodyPart()
        val source = FileDataSource(file)
        mimeFile.dataHandler = DataHandler(source)
        mimeFile.fileName = file.name
        multipart.addBodyPart(mimeFile)

        email.setContent(multipart)

        return email
    }

    private fun createMessageWithEmail(emailContent: MimeMessage): Message {
        val buffer = ByteArrayOutputStream()
        emailContent.writeTo(buffer)

        val bytes = buffer.toByteArray()
        val encodedEmail = Base64.encodeBase64URLSafeString(bytes)
        val message = Message()
        message.raw = encodedEmail

        return message
    }

    private fun sendMessage(service: Gmail,
                    userId: String,
                    emailContent: MimeMessage) {
        val message = createMessageWithEmail(emailContent)
        service.users().messages().send(userId, message).execute()
    }

    companion object {
        private const val TAG = "EmailSender"
        private const val PREF_ACCOUNT_NAME = "accountName"
        private const val RC_ACCOUNT_CHOOSER = 1001
        private const val RC_AUTHZ = 1002
        private val SCOPES = listOf(GmailScopes.GMAIL_SEND)

        private const val FROM_PERSONAL = "Brian and Angela Dang"
        private const val SUBJECT = "Thank You for a Dang Good Time!"
        private const val BODY = "Thanks for being a part of the best Dang wedding!\n\nLove,\nBrian and Angela Dang"
    }
}

class SendEmailException(val email: String, e: Exception) : Exception(e)