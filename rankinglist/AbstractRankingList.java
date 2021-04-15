package com.xxxx.rankinglist;

/**
 * 排名榜单 抽象实现
 *
 * @author <a href='jwtao520@qq.com'>Jvvtao<a/>
 * @since
 */
public abstract class AbstractRankingList implements Ranking {

    private final long timestamp = 1893427200; // 2030-01-01 00:00:00

    /**
     * 给 score 加上时间权重（秒级），时间越早权重越高
     *
     * @param score
     * @return
     */
    protected final double getScoreWithTimeWeight(int score) {
        long timeDifference = timestamp - System.currentTimeMillis() / 1000;
        return Double.valueOf(String.format("%d.%d", score, timeDifference));
    }
}
