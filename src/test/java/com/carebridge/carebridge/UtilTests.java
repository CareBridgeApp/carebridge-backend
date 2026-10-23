package com.carebridge.carebridge;

import com.carebridge.carebridge.Utils.MedicineDateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

public class UtilTests {
    private final MedicineDateUtil utils = new MedicineDateUtil();
    @Test
    public void getIsoDate() {
        LocalDate EndDate=utils.calculateEndDate(LocalDate.now(),"7 days");
        System.out.println(EndDate);
    }

}
