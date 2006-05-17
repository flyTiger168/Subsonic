package net.sourceforge.subsonic.controller;

import org.springframework.web.servlet.mvc.*;
import net.sourceforge.subsonic.service.*;
import net.sourceforge.subsonic.command.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Controller for the page used to administrate general settings.
 *
 * @author Sindre Mehus
 */
public class GeneralSettingsController extends SimpleFormController {

    private SettingsService settingsService;
    private InternationalizationService internationalizationService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        GeneralSettingsCommand command = new GeneralSettingsCommand();
        command.setCoverArtLimit(String.valueOf(settingsService.getCoverArtLimit()));
        command.setCoverArtMask(settingsService.getCoverArtMask());
        command.setDownloadLimit(String.valueOf(settingsService.getDownloadBitrateLimit()));
        command.setUploadLimit(String.valueOf(settingsService.getUploadBitrateLimit()));
        command.setIgnoredArticles(settingsService.getIgnoredArticles());
        command.setQuickLinks(settingsService.getQuickLinks());
        command.setIndex(settingsService.getIndexString());
        command.setMusicMask(settingsService.getMusicMask());
        command.setPlaylistFolder(settingsService.getPlaylistFolder());
        command.setWelcomeMessage(settingsService.getWelcomeMessage());

        Locale currentLocale = internationalizationService.getLocale();
        Locale[] locales = internationalizationService.getAvailableLocales();
        String[] localeStrings = new String[locales.length];
        for (int i = 0; i < locales.length; i++) {
            localeStrings[i] = locales[i].getDisplayLanguage(locales[i]);

            if (currentLocale.equals(locales[i])) {
                command.setLocaleIndex(String.valueOf(i));
            }
        }
        command.setLocales(localeStrings);

        return command;

    }

    protected void doSubmitAction(Object comm) throws Exception {
        GeneralSettingsCommand command = (GeneralSettingsCommand) comm;

        int localeIndex = Integer.parseInt(command.getLocaleIndex());
        Locale locale = internationalizationService.getAvailableLocales()[localeIndex];

        command.setReloadNeeded(!settingsService.getIndexString().equals(command.getIndex()) ||
                                !settingsService.getIgnoredArticles().equals(command.getIgnoredArticles()) ||
                                !settingsService.getQuickLinks().equals(command.getQuickLinks()) ||
                                !internationalizationService.getLocale().equals(locale));

        settingsService.setIndexString(command.getIndex());
        settingsService.setIgnoredArticles(command.getIgnoredArticles());
        settingsService.setQuickLinks(command.getQuickLinks());
        settingsService.setPlaylistFolder(command.getPlaylistFolder());
        settingsService.setMusicMask(command.getMusicMask());
        settingsService.setCoverArtMask(command.getCoverArtMask());
        settingsService.setWelcomeMessage(command.getWelcomeMessage());
        try {
            settingsService.setCoverArtLimit(Integer.parseInt(command.getCoverArtLimit()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }
        try {
            settingsService.setDownloadBitrateLimit(Long.parseLong(command.getDownloadLimit()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }
        try {
            settingsService.setUploadBitrateLimit(Long.parseLong(command.getUploadLimit()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }
        settingsService.save();

        internationalizationService.setLocale(locale);
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setInternationalizationService(InternationalizationService internationalizationService) {
        this.internationalizationService = internationalizationService;
    }
}
