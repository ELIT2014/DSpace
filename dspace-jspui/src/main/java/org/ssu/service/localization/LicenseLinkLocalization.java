package org.ssu.service.localization;

import org.dspace.app.util.DCInputsReaderException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class LicenseLinkLocalization {
    private Map<Locale, LocalizedLicenseStorage> licenseLinks = new HashMap<>();

    class LocalizedLicenseStorage{
        private Map<String, String> typesTable = new HashMap<>();

        public LocalizedLicenseStorage(Map<String, String> typesTable) {
            this.typesTable = typesTable;
        }

        public String getTypeLocalized(String type) {
            return typesTable.getOrDefault(type, type);
        }
    }

    @PostConstruct
    public void init() {
        licenseLinks.put(Locale.ENGLISH, updateLicenseLocalizationTable(Locale.ENGLISH));
        licenseLinks.put(Locale.forLanguageTag("uk"), updateLicenseLocalizationTable(Locale.forLanguageTag("uk")));
        licenseLinks.put(Locale.forLanguageTag("ru"), updateLicenseLocalizationTable(Locale.forLanguageTag("ru")));
    }

    private LocalizedLicenseStorage updateLicenseLocalizationTable(Locale locale) {
        Map<String, String> typesTable = new HashMap<>();
        List<String> typesList = null;
        try {
            typesList = new LocalizedInputsReader().getInputsReader(locale.getLanguage()).getPairs("rights_links_localization");
        } catch (DCInputsReaderException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < typesList.size(); i += 2)
            typesTable.put(typesList.get(i + 1), typesList.get(i));
        return new LocalizedLicenseStorage(typesTable);
    }

    public String getLicenseLink(String type, Locale locale) {
        return licenseLinks.get(locale).getTypeLocalized(type);
    }
}
