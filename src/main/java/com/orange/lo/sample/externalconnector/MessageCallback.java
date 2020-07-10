/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.externalconnector;

import com.orange.lo.sample.lo.model.CommandRequest;

public interface MessageCallback {
    void onMessage(String nodeId, CommandRequest commandRequest);
}
