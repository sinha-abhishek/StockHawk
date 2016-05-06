package com.sam_chordas.android.stockhawk.mvp;

import android.os.Bundle;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by abhishek on 04/05/16.
 */
public class ModelCollectionManager {
    private static final String SIS_KEY_PRESENTER_ID = "presenter_id";
    private static ModelCollectionManager instance;

    private final AtomicLong currentId;

    private final Cache<Long, BaseModelCollection<?, ?>> presenters;

    ModelCollectionManager(long maxSize, long expirationValue, TimeUnit expirationUnit) {
        currentId = new AtomicLong();

        presenters = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expirationValue, expirationUnit)
                .build();
    }

    public static ModelCollectionManager getInstance() {
        if (instance == null) {
            instance = new ModelCollectionManager(10, 30, TimeUnit.SECONDS);
        }
        return instance;
    }

    public <P extends BaseModelCollection<?, ?>> P restoreModels(Bundle savedInstanceState) {
        Long presenterId = savedInstanceState.getLong(SIS_KEY_PRESENTER_ID);
        P presenter = (P) presenters.getIfPresent(presenterId);
        presenters.invalidate(presenterId);
        return presenter;
    }

    public void saveModels(BaseModelCollection<?, ?> presenter, Bundle outState) {
        long presenterId = currentId.incrementAndGet();
        presenters.put(presenterId, presenter);
        outState.putLong(SIS_KEY_PRESENTER_ID, presenterId);
    }
}
