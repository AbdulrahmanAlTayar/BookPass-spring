package com.bookpass.bookpass.constants;

import lombok.Getter;

@Getter
public enum SaudiUniversities {
    KSU("KSU", "King Saud University", "ksu.edu.sa"),
    KFUPM("KFUPM", "King Fahd University of Petroleum and Minerals", "kfupm.edu.sa"),
    KAU("KAU", "King Abdulaziz University", "kau.edu.sa"),
    PNU("PNU", "Princess Nourah bint Abdulrahman University", "pnu.edu.sa"),
    QU("QU", "Qassim University", "qu.edu.sa"),
    KKU("KKU", "King Khalid University", "kku.edu.sa"),
    UJ("UJ", "University of Jeddah", "uj.edu.sa"),
    KFU("KFU", "King Faisal University", "kfu.edu.sa"),
    IMAMU("IMAMU", "Imam Mohammad Ibn Saud Islamic University", "imamu.edu.sa"),
    UQU("UQU", "Umm Al-Qura University", "uqu.edu.sa"),
    TU("TU", "Taif University", "tu.edu.sa"),
    HGU("HGU", "Hail University", "uoh.edu.sa"), // UOH is University of Hail
    JU("JU", "Jazan University", "jazanu.edu.sa"),
    NU("NU", "Najran University", "nu.edu.sa"),
    BU("BU", "Al-Baha University", "bu.edu.sa"),
    TU_TABUK("TU_TABUK", "Tabuk University", "ut.edu.sa"),
    JOUF("JOUF", "Jouf University", "ju.edu.sa"),
    NB("NB", "Northern Border University", "nbu.edu.sa"),
    SEU("SEU", "Saudi Electronic University", "seu.edu.sa");

    private final String acronym;
    private final String name;
    private final String emailDomain;

    SaudiUniversities(String acronym, String name, String emailDomain) {
        this.acronym = acronym;
        this.name = name;
        this.emailDomain = emailDomain;
    }
}
