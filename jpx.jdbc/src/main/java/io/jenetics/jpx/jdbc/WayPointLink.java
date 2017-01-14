/*
 * Java GPX Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.jpx.jdbc;

import static java.lang.String.format;

import io.jenetics.jpx.jdbc.internal.util.Pair;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class WayPointLink {
	private final long _wayPointID;
	private final long _linkID;

	private WayPointLink(final long wayPointID, final long linkID) {
		_wayPointID = wayPointID;
		_linkID = linkID;
	}

	long getWayPointID() {
		return _wayPointID;
	}

	long getLinkID() {
		return _linkID;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += 37*Long.hashCode(_wayPointID) + 31;
		hash += 37*Long.hashCode(_linkID) + 31;

		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof WayPointLink &&
			Long.compare(((WayPointLink)obj)._wayPointID, _wayPointID) == 0 &&
			Long.compare(((WayPointLink)obj)._linkID, _linkID) == 0;
	}

	@Override
	public String toString() {
		return format("MetadataLink[%d, %d]", _wayPointID, _linkID);
	}

	public static WayPointLink of(final long metadataID, final long linkID) {
		return new WayPointLink(metadataID, linkID);
	}

	public static WayPointLink of(final Pair<Long, Long> pair) {
		return new WayPointLink(pair._1, pair._2);
	}

}
