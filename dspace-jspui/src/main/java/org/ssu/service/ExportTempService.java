package org.ssu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dspace.browse.BrowseInfo;
import org.dspace.content.Item;
import org.springframework.stereotype.Service;
import org.ssu.entity.Publication;
import org.ssu.service.localization.TypeLocalization;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Service
public class ExportTempService {
    @Resource
    private TypeLocalization typeLocalization;

    @Resource
    private ItemService itemService;

    public String publicationList(BrowseInfo browseInfo) {
        List<Item> items = browseInfo.getBrowseItemResults();
        return parsePublications(items);
    }

    private String parsePublications(List<Item> items) {
        try {
            return new ObjectMapper()
                    .writeValueAsString(items.stream()
                            .map(this::parseData)
                            .collect(Collectors.toList()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private Publication parseData(Item item) {
        Locale ukrainianLocale = Locale.forLanguageTag("uk");
        String localizedAuthors = itemService.extractAuthorListForItem(item)
                .stream()
                .map(author -> String.format("%s %s", author.getSurname(ukrainianLocale), author.getInitials(ukrainianLocale)))
                .collect(Collectors.joining(";\r\n"));

        return new Publication.Builder()
                .withAuthors(localizedAuthors)
                .withCitation(itemService.getCitationForItem(item))
                .withTitle(item.getName())
                .withType(itemService.getItemTypeLocalized(item, ukrainianLocale))
                .build();
    }
}