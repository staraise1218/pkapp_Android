package com.zhongyi.mind.data.temp;

import com.zhongyi.mind.data.BaseNetBean;
import com.zhongyi.mind.data.User;

public class UserTemp extends BaseNetBean {
    private User data;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
