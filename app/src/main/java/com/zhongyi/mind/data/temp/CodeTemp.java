package com.zhongyi.mind.data.temp;

import com.zhongyi.mind.data.BaseNetBean;
import com.zhongyi.mind.data.VerifCode;

public class CodeTemp extends BaseNetBean {
    private VerifCode data;

    public VerifCode getData() {
        return data;
    }

    public void setData(VerifCode data) {
        this.data = data;
    }
}
