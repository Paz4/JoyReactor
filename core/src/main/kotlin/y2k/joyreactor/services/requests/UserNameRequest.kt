package y2k.joyreactor.services.requests

import rx.Observable
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.ioObservable

/**
 * Created by y2k on 10/4/15.
 */
class UserNameRequest(private val httpClient: HttpClient) {

    fun request(): Observable<String?> {
        return ioObservable {
            val document = httpClient.getDocument("http://joyreactor.cc/donate")
            document.select("a#settings").first()?.text()
        }
    }
}