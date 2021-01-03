package cn.th.teabag.entity;

import lombok.Data;

@Data
public class BeatMapInfo {
    private double difficultyStar;
    private Long beatMapId;
    private Long beatMapSetsId;
    private String version;
    private Double od;
    private Double ar;
    private Double bpm;
    private Double cs;
    private Double hp;
    private Long combo;
    private String status;
}
