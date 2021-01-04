package cn.th.teabag.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Mods {

    NM(0L,"NM"),
    NF(1L,"NF"),
    EZ(2L,"EZ"),
    TD(4L,"TD"),
    HD(8L,"HD"),
    HR(16L,"HR"),
    SD(32L,"SD"),
    DT(64L,"DT"),
    RX(128L,"RX"),
    HT(256L,"HT"),
    NC(576L,"NC"), // (512) Only set along with DoubleTime. i.e: NC only gives 576
    FL(1024L,"FL"),
    AUTO(2048L,"AT"),
    SO(4096L,"SO"),
    AP(8192L,"AP"),
    PF(16416L,"PF");//(16384) Only set along with SuddenDeath. i.e: PF only gives 16416
    private final Long id;
    private final String modeName;



}
