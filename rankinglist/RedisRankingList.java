package com.xxxx.rankinglist;

import com.xxxx.redis.RedisService;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Tuple;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 排行榜 Redis 实现
 *
 * @author <a href='jwtao520@qq.com'>Jvvtao<a/>
 * @since
 */
@Component
public class RedisRankingList extends AbstractRankingList {

    @Resource
    private RedisService redisService;

    @Override
    public boolean push(String key, String member, int score, int limit) {
        double scoreWithTimeWeight = getScoreWithTimeWeight(score);

        if (!isOverLimit(key, limit)) {
            return push(key, member, scoreWithTimeWeight);
        }

        if (isExistMember(key, member)) {
            return push(key, member, scoreWithTimeWeight);
        }

        return pushAndPop(key, member, scoreWithTimeWeight);
    }

    private boolean push(String key, String member, double score) {
        redisService.zadd(key, score, member);
        return true;
    }

    private boolean pushAndPop(String key, String member, double score) {
        Set<Tuple> tupleSet = redisService.zrangeWithScores(key, 0, 0);
        if (!ObjectUtils.isEmpty(tupleSet)) {

            for (Tuple tuple : tupleSet) {
                if (tuple.getScore() < score) {
                    redisService.zadd(key, score, member);
                    redisService.zremrangeByRank(key, 0, 0);
                    return true;
                }
                return false;
            }
        }

        redisService.zadd(key, score, member);
        return true;
    }

    private boolean isExistMember(String key, String member) {
        Double existScore = redisService.zscore(key, member);
        if (existScore == null) {
            return false;
        }
        return true;
    }

    private boolean isOverLimit(String key, int limit) {
        long count = redisService.zcard(key);
        if (count < limit) {
            return false;
        }
        return true;
    }

    @Override
    public int rank(String key, String member) {
        Long no = redisService.zrevrank(key, member);
        if (no == null) {
            return -1;
        }
        return no.intValue() + 1;
    }

    @Override
    public Set<Tuple> listScore(String key, int limit) {
        return redisService.zrevrangeWithScores(key, 0, limit - 1);
    }

    @Override
    public Set<String> list(String key, int limit) {
        return redisService.zrevrange(key, 0, limit - 1);
    }

    @Override
    public Set<String> list(String key, int offset, int endOffset) {
        return redisService.zrevrange(key, offset, endOffset - 1);
    }

    @Override
    public int getMinRankScore(String key, int rank) {
        Set<Tuple> set = redisService.zrevrangeWithScores(key, rank - 1, rank - 1);
        for (Tuple tuple : set) {
            return Double.valueOf(tuple.getScore()).intValue();
        }

        set = redisService.zrangeWithScores(key, 0, 0);
        for (Tuple tuple : set) {
            return Double.valueOf(tuple.getScore()).intValue();
        }
        return -1;
    }
}
