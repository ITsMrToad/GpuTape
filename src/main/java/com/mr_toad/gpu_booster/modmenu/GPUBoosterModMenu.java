package com.mr_toad.gpu_booster.modmenu;

import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.lib.api.client.screen.config.ToadConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class GPUBoosterModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ToadConfigScreen(parent, GPUBooster.CONFIG);
    }
}
