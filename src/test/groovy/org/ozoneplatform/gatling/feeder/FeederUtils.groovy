package org.ozoneplatform.gatling.feeder

class FeederUtils {

    public final static String DICTIONARY_FILE_PATH = 'dictionary.txt'
    public final static String CORPUS_FILE_PATH = 'lotsatext.txt'

    public static String getBaseUrl() {
        String baseUrl = System.properties.getProperty('baseUrl') ?: 'https://localhost:8443/marketplace/'
        baseUrl.endsWith('/') ? baseUrl : baseUrl + '/'
    }

    public static Integer getAdminCount() { (System.properties.getProperty('adminCount') ?: '1') as Integer }

    public static Integer getUserCount() { (System.properties.getProperty('userCount') ?: '1') as Integer }

    public static Integer getItemCount() { (System.properties.getProperty('itemCount') ?: '1') as Integer }

    public static String[] getDictionaryWords() { new File(DICTIONARY_FILE_PATH).readLines() as String[] }

    public static String getTextCorpus() { new File(CORPUS_FILE_PATH).withReader { it.getText() }}

    public static String[] getWordsDistro() { textCorpus.split() }

    public static String getStoreItemsAsJsonString() { ['curl', '-XGET', '-utestAdmin1:password', baseUrl + 'api/serviceItem'].execute().text }
}
