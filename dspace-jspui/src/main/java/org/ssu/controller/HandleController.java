package org.ssu.controller;

import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.handle.service.HandleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.ssu.service.ItemService;
import org.ssu.service.statistics.EssuirStatistics;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class HandleController {
    private HandleService handleService = HandleServiceFactory.getInstance().getHandleService();
    private AuthorizeService authorizeService = AuthorizeServiceFactory.getInstance().getAuthorizeService();
    @Resource
    private ItemService itemService;

    @Resource
    private EssuirStatistics essuirStatistics;

    @RequestMapping(value = "/123456789/{itemId}")
    public ModelAndView entrypoint(HttpServletRequest request, @PathVariable("itemId") String itemId, ModelAndView model) throws SQLException {
        Context dspaceContext = UIUtil.obtainContext(request);
        DSpaceObject dSpaceObject = handleService.resolveToObject(dspaceContext, "123456789/" + itemId);
        Locale locale = dspaceContext.getCurrentLocale();
        if(authorizeService.authorizeActionBoolean(dspaceContext, dSpaceObject, Constants.READ)) {
            if (dSpaceObject.getType() == Constants.ITEM) {
                return displayItem(request, model, (Item) dSpaceObject, locale);
            }

            System.out.println(dSpaceObject.getType());
        }
        return null;
    }

    private ModelAndView displayItem(HttpServletRequest request, ModelAndView model, Item item, Locale locale) {
        essuirStatistics.updateItemViews(request, item.getLegacyId());

        List<String> authors = itemService.extractAuthorListForItem(item).stream()
                .map(author -> String.format("%s, %s", author.getSurname(locale), author.getInitials(locale)))
                .collect(Collectors.toList());

        model.addObject("title", item.getName());
        model.addObject("titleAlternative", itemService.getAlternativeTitleForItem(item));
        model.addObject("owningCollections", item.getCollections());
        model.addObject("type", itemService.getItemTypeLocalized(item, locale));
        model.addObject("authors", authors);
        model.addObject("keywords", itemService.getKeywordsForItem(item));
        model.addObject("year", itemService.extractIssuedYearForItem(item));
        model.addObject("uri", itemService.getURIForItem(item));
        model.addObject("publisher", itemService.getPublisherForItem(item));
        model.addObject("citation", itemService.getCitationForItem(item));
        model.addObject("abstracts", itemService.getAbstractsForItem(item));

        model.setViewName("item-display");
        return model;
    }
}
