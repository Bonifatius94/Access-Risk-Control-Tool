package ui.main.configs;

import data.entities.Configuration;

public class ConfigsFormController {

    private Configuration configuration;



    public void giveSelectedConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
