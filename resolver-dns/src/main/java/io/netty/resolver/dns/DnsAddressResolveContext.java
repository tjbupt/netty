/*
 * Copyright 2018 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.resolver.dns;

import static io.netty.resolver.dns.DnsAddressDecoder.decodeAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
<<<<<<< HEAD
=======
import java.util.Collections;
>>>>>>> dev
import java.util.List;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.util.concurrent.Promise;

final class DnsAddressResolveContext extends DnsResolveContext<InetAddress> {

    private final DnsCache resolveCache;
    private final AuthoritativeDnsServerCache authoritativeDnsServerCache;
    private final boolean completeEarlyIfPossible;

    DnsAddressResolveContext(DnsNameResolver parent, Promise<?> originalPromise,
                             String hostname, DnsRecord[] additionals,
                             DnsServerAddressStream nameServerAddrs, int allowedQueries, DnsCache resolveCache,
                             AuthoritativeDnsServerCache authoritativeDnsServerCache,
                             boolean completeEarlyIfPossible) {
        super(parent, originalPromise, hostname, DnsRecord.CLASS_IN,
              parent.resolveRecordTypes(), additionals, nameServerAddrs, allowedQueries);
        this.resolveCache = resolveCache;
        this.authoritativeDnsServerCache = authoritativeDnsServerCache;
        this.completeEarlyIfPossible = completeEarlyIfPossible;
    }

    @Override
    DnsResolveContext<InetAddress> newResolverContext(DnsNameResolver parent, Promise<?> originalPromise,
                                                      String hostname,
                                                      int dnsClass, DnsRecordType[] expectedTypes,
                                                      DnsRecord[] additionals,
                                                      DnsServerAddressStream nameServerAddrs, int allowedQueries) {
        return new DnsAddressResolveContext(parent, originalPromise, hostname, additionals, nameServerAddrs,
                allowedQueries, resolveCache, authoritativeDnsServerCache, completeEarlyIfPossible);
    }

    @Override
    InetAddress convertRecord(DnsRecord record, String hostname, DnsRecord[] additionals, EventLoop eventLoop) {
        return decodeAddress(record, hostname, parent.isDecodeIdn());
    }

    @Override
    List<InetAddress> filterResults(List<InetAddress> unfiltered) {
<<<<<<< HEAD
        unfiltered.sort(PreferredAddressTypeComparator.comparator(parent.preferredAddressType()));
=======
        Collections.sort(unfiltered, PreferredAddressTypeComparator.comparator(parent.preferredAddressType()));
>>>>>>> dev
        return unfiltered;
    }

    @Override
    boolean isCompleteEarly(InetAddress resolved) {
        return completeEarlyIfPossible && parent.preferredAddressType().addressType() == resolved.getClass();
    }

    @Override
    boolean isDuplicateAllowed() {
        // We don't want include duplicates to mimic JDK behaviour.
        return false;
    }

    @Override
    void cache(String hostname, DnsRecord[] additionals,
               DnsRecord result, InetAddress convertedResult) {
        resolveCache.cache(hostname, additionals, convertedResult, result.timeToLive(), parent.ch.eventLoop());
    }

    @Override
    void cache(String hostname, DnsRecord[] additionals, UnknownHostException cause) {
        resolveCache.cache(hostname, additionals, cause, parent.ch.eventLoop());
    }

    @Override
    void doSearchDomainQuery(String hostname, Promise<List<InetAddress>> nextPromise) {
        // Query the cache for the hostname first and only do a query if we could not find it in the cache.
        if (!DnsNameResolver.doResolveAllCached(
                hostname, additionals, nextPromise, resolveCache, parent.resolvedInternetProtocolFamiliesUnsafe())) {
            super.doSearchDomainQuery(hostname, nextPromise);
        }
    }

    @Override
    DnsCache resolveCache() {
        return resolveCache;
    }

    @Override
    AuthoritativeDnsServerCache authoritativeDnsServerCache() {
        return authoritativeDnsServerCache;
    }
}
