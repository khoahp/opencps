package org.opencps.lucenequery.util;

import java.util.ArrayList;
import java.util.List;

import org.opencps.lucenequery.model.LuceneMenu;
import org.opencps.lucenequery.service.LuceneMenuLocalServiceUtil;

public class LuceneMenuUtil {
	public static List<LuceneMenu> buildTreeMenu(
			List<LuceneMenu> rootMenuItems, List<LuceneMenu> treeMenu,
			long groupId, long menuGroupId) {
		
		if (rootMenuItems != null) {
			for (LuceneMenu luceneMenu : rootMenuItems) {
				//System.out.println(luceneMenu.getName());
				// Add menu to tree

				treeMenu.add(luceneMenu);

				List<LuceneMenu> childMenus = new ArrayList<LuceneMenu>();
				try {
					childMenus = LuceneMenuLocalServiceUtil
							.getLuceneMenusByG_MG_P(groupId, menuGroupId,
									luceneMenu.getMenuItemId());
				} catch (Exception e) {
					continue;
				}

				if (childMenus != null && !childMenus.isEmpty()) {
					buildTreeMenu(childMenus, treeMenu, groupId, menuGroupId);
				}

			}

		}

		return treeMenu;
	}
}
