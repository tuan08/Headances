package org.headvances.crawler.channel;

import java.io.Serializable;

public interface ChannelGateway {
	public void send(final Serializable object) ;
}