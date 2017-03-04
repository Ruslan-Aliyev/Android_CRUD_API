package com.ruslan_website.travelblog.utils.http.api;

import com.ruslan_website.travelblog.utils.http.api.cms.Cake;
import com.ruslan_website.travelblog.utils.http.api.cms.Laravel;

public class APIFactory {

    private APIStrategy apiStrategy;

    public APIFactory(String backendOption){

        switch(backendOption) {
            case "laravel" :
                apiStrategy = new Laravel();
                break;

            case "cake" :
                apiStrategy = new Cake();
                break;

            default :
                apiStrategy = new Laravel();
        }

    }

    public APIStrategy getApiStrategy() {
        return apiStrategy;
    }
}
