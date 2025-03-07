package cn.langya.simgaaura;

import tech.konata.phosphate.api.Extension;
import tech.konata.phosphate.api.PApi;
import cn.langya.simgaaura.module.SimgaAura;

/**
 * @author IzumiiKonata
 * Date: 2025/1/27 21:26
 */
public class Main extends Extension {

    @Override
    public void onLoad(PApi pApi) {
        System.out.println("Simga Aura extension is loaded!");

        // 注册模块
        pApi.registerModule(new SimgaAura());
    }

    @Override
    public void onStop(PApi pApi) {
        System.out.println("Simga Aura extension is unloaded!");
    }

    @Override
    public String getName() {
        return "Simga Aura extension";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
}
