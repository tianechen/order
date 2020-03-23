package com.lesson.service.impl;

import com.lesson.model.Menu;
import com.lesson.dao.MenuDAO;
import com.lesson.service.MenuManager;
import javafx.util.Builder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.xmind.core.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class MenuManagerImpl implements MenuManager {
    Logger logger = Logger.getLogger(MenuManagerImpl.class);

   final static List<String> FIRSTNAME = new ArrayList<String>();

    @Autowired
    MenuDAO menuDAO;

    public List<Menu> getAllMenus() {
        return menuDAO.getAllMenus();
    }

    public List<Menu> getMenusByMidCid(String mid, String cid) {
        return menuDAO.getMenuByMidCid(mid, cid);
    }

    public Menu getMenuByMid(String mid) {
        List<Menu> menus = menuDAO.getMenuByMidCid(mid, "%");

        if (menus == null) { //如果是空直接返回null
            logger.info("查询menu无返回接口，请检查后台是否出错！");
            return null;
        }

        int size = menus.size();

        if (size == 0) {
            logger.info("菜品查询返回结果为空 mid = " + mid);
            return null;
        } else if (size > 1) {
            logger.error("DB mid 重复 mid = " + mid);
            return null;
        }

        return menus.get(0);
    }

    public int addMenu(int cid, String mname, float price) {
        logger.info("添加菜品 cid = " + cid + ", mname = " + mname + ", price = " + price);
        return menuDAO.addMenu(cid, mname, price);
    }

    public int updateMenuByMid(int mid, int cid, String mname, float price) {
        logger.info("更新菜品详情 mid = " + mid + ", cid = " + cid + ", mname = " + mname + ", price = " + price);
        return menuDAO.updateMenuByMid(mid, cid, mname, price);
    }

    public int deleteMenuByMid(int mid) {
        int inpactRowNum = menuDAO.deleteMenuByMid(mid);
        if (inpactRowNum == 1) {
            logger.info("对应菜品已被删除，mid = " + mid);
        } else {
            logger.info("对应菜品删除失败, mid = " + mid);
        }
        return inpactRowNum;
    }


    // 解析xmind
    public static void main(String[] args) {
        IWorkbookBuilder builder = Core.getWorkbookBuilder();// 初始化builder
        IWorkbook workbook = null;
        try {
        File file = new File("D:\\cte\\auto_case\\shop.xmind");
        workbook = builder.loadFromFile(file);// 打开XMind文件
        } catch (Exception e) {

        }
        ISheet defSheet = workbook.getPrimarySheet();// 获取主Sheet
        ITopic rootTopic = defSheet.getRootTopic(); // 获取根Topic
        String className = rootTopic.getTitleText();//节点TitleText
        List<ITopic> iTopics = rootTopic.getAllChildren();//获取所有子节点
        //调用如下
        StringBuilder sb = new StringBuilder();
        sb.append(className);//放入根节点名称
        GetXmind(iTopics,sb);
    }

    private static boolean GetXmind(List<ITopic> list, StringBuilder name){
        String lastName = "";
        if(CollectionUtils.isEmpty(list)) {
            return false;
        }
        if(list.size() == 1 && CollectionUtils.isEmpty(list.get(0).getAllChildren())) {
            lastName = list.get(0).getTitleText();
            System.out.println("firstName:"+name.toString());
            System.out.println("lastName:"+lastName);

            //System.out.println(name.toString()+"/"+list.get(0).getTitleText());
            return true;
        }
        for (ITopic iTopic : list) {
            StringBuilder sb = new StringBuilder();
            sb.append(name.toString());
            String firstName = sb.toString();
            sb.append("/").append(iTopic.getTitleText());
            boolean state = GetXmind(iTopic.getAllChildren(), sb);
            if(!state){
                lastName = iTopic.getTitleText();
//                if (!FIRSTNAME.contains(firstName)){
//                    System.out.println("firstName:"+firstName);
//                    FIRSTNAME.add(firstName);
//                }
                System.out.println("firstName:"+firstName);
                System.out.println("lastName:"+lastName);


                //System.out.println(sb.toString());
            }

        }
        return true;
    }
}
