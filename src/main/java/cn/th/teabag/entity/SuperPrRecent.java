package cn.th.teabag.entity;

import lombok.Data;

@Data
public class SuperPrRecent {
    private Double acc;
    private int maxCombo;
    private String score;
    private int pp;
    private String mods;
    private int num_50;
    private int num_100;
    private int num_300;
    private int num_miss;
    private String rank;
    private boolean isSS;
    private String createAt;
    private BeatMapInfo beatMapInfo;
    private BeatMapSetsInfo beatMapSetsInfo;
    private User user;
}
