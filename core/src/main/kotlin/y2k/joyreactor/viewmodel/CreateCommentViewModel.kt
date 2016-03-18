package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.binding
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.model.Image
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.services.CommentService
import y2k.joyreactor.services.ProfileService

/**
 * Created by y2k on 3/8/16.
 */
class CreateCommentViewModel(
    private val profileService: ProfileService,
    private val service: CommentService) {

    val isBusy = binding(false)

    val username = binding("")
    val avatar = binding<Image>()

    val commentText = binding("")

    init {
        profileService
            .getProfile()
            .subscribeOnMain {
                username.value = it.userName
                avatar.value = it.userImage
            }
    }

    fun create() {
        isBusy.value = true
        service
            .createComment("2219757", commentText.value) // FIXME:
            .subscribeOnMain {
                NavigationService.instance.closeCreateComment()
                isBusy.value = false
            }
    }
}