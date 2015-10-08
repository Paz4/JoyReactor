package y2k.joyreactor;

import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 06/10/15.
 */
public class PostListService {

    private List<Post> buffer = new ArrayList<>();
    private String nextPageId;
    private Tag tag;

    public void setCurrentTag(Tag tag) {
        this.tag = tag;
        buffer.clear();
        nextPageId = null;
    }

    public Observable<Void> loadNextPageAsync() {
        return ObservableUtils.create(() -> {
            PostRequest request = new PostRequest(tag == null ? null : tag.getId(), nextPageId);
            request.request();
            return request;
        }).map(request -> {
            new PostMerger().addNewPosts(request);
            nextPageId = request.nextPageId;
            return null;
        });
    }

    public Observable<List<Post>> getList() {
        return Observable.just(buffer);
    }

    class PostMerger {

        void addNewPosts(PostRequest request) {
            for (Post post : request.posts)
                if (isNew(post)) buffer.add(post);
        }

        private boolean isNew(Post post) {
            for (Post oldPost : buffer)
                if (oldPost.id.equals(post.id)) return false;
            return true;
        }
    }
}