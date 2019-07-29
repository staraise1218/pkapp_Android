package com.zhongyi.mind.data;

import java.util.List;
public class HomeData {
    private List<BannerBean> bannerList;
    private List<DynamicBean> dynamiclist;
    private List<Artical> articleList;

    public List<BannerBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerBean> bannerList) {
        this.bannerList = bannerList;
    }

    public List<DynamicBean> getDynamiclist() {
        return dynamiclist;
    }

    public void setDynamiclist(List<DynamicBean> dynamiclist) {
        this.dynamiclist = dynamiclist;
    }

    public List<Artical> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Artical> articleList) {
        this.articleList = articleList;
    }
}
