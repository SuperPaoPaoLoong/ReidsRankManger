package com.xxxx.rankinglist;

import redis.clients.jedis.Tuple;

import java.util.Set;

/**
 * 排名 接口
 *
 * @author <a href='jwtao520@qq.com'>Jvvtao<a/>
 * @since
 */
public interface Ranking {

    boolean push(String key, String member, int score, int limit);

    int rank(String key, String member);

    Set<String> list(String key, int limit);

    Set<Tuple> listScore(String key, int limit);

    Set<String> list(String key, int offset, int endOffset);

    int getMinRankScore(String key, int rank);
}
