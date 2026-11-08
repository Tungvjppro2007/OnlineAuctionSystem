
package com.auction.common.patterns;

import com.auction.common.payload.Message;

public interface Observer {
    void update(Message message);
}