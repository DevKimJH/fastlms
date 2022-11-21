package com.zerobase.fastlms.course.model;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BannerInput {
    long id;

    int categoryId;

    String subject;
    String urlAddress;
    int sortValue;
    boolean postYn;
    String filename;
    String urlFilename;

    String idList;
}
