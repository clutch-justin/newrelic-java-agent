/*
 *
 *  * Copyright 2021 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.mongodb.async.client;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.operation.AsyncOperationExecutor_Instrumentation;
import com.mongodb.session.ClientSession;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.nr.agent.mongo.MongoUtil;
import org.bson.codecs.configuration.CodecRegistry;

@Weave(type = MatchType.ExactClass, originalName = "com/mongodb/async/client/ListCollectionsIterableImpl")
abstract class ListCollectionsIterableImpl_Instrumentation<TResult> extends MongoIterableImpl_Instrumentation<TResult> {

    ListCollectionsIterableImpl_Instrumentation(final ClientSession clientSession, final String databaseName, final Class<TResult> resultClass,
            final CodecRegistry codecRegistry,
            final ReadPreference readPreference, final AsyncOperationExecutor_Instrumentation executor) {
        super(clientSession, executor, ReadConcern.DEFAULT, readPreference);
        super.collectionName = "collections";
        super.databaseName = databaseName;
        super.operationName = MongoUtil.OP_LIST_COLLECTIONS;
    }

}