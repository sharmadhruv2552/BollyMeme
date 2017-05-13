package dhruv.io.bollymeme.utils;

import dhruv.io.bollymeme.activities.VideoPlayActivity;

/**
 * Created by Prithvi on 24-Apr-17.
 */

public interface HpLibRendererBuilder {

    void buildRenderers(VideoPlayActivity player);
    void cancel();
}
