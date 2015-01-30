package com.shengshi.rebate.bean.detail;

import java.io.Serializable;
import java.util.List;

public class RecordEntity implements Serializable {

    private static final long serialVersionUID = -7345914821902546335L;
    public List<Record> records;

    public class Record implements Serializable {
        private static final long serialVersionUID = 163061132230418419L;

        public String amount;
        public String date;
        public String status;
        public String tailNumber;
        public String cardOwner;

    }

}
