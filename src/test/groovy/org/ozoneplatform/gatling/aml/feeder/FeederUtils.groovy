package org.ozoneplatform.gatling.aml.feeder

class FeederUtils {

    public final static String DICTIONARY_FILE_PATH = 'dictionary.txt'
    public final static String CORPUS_FILE_PATH = 'wordDistro.txt'
    public final static String SERVICE_ITEM_PATH = 'api/listing'
    public final static String PROFILE_PATH = 'api/profile'
    public final static String TYPE_PATH = 'api/type'
    public final static String CATEGORY_PATH = 'api/category'
    public final static String CONTACT_TYPE_PATH = 'api/contactType'
    public final static String AGENCY_PATH = 'api/agency'
    public final static String IMAGE_PATH = 'api/image'


    public static String getBaseUrl() {
        String baseUrl = System.properties.getProperty('baseUrl') ?: 'https://localhost:8443/marketplace/'
        baseUrl.endsWith('/') ? baseUrl : baseUrl + '/'
    }

    public static Integer getAdminCount() { (System.properties.getProperty('adminCount') ?: '1') as Integer }

    public static Integer getUserCount() { (System.properties.getProperty('userCount') ?: '1') as Integer }

    public static Integer getItemCount() { (System.properties.getProperty('itemCount') ?: '1') as Integer }

    public static Integer getMetaDataCount() { (System.properties.getProperty('metaDataCount') ?: '10') as Integer }

    public static Integer getActionPercentage() { (System.properties.getProperty('actionPercentage') ?: '5') as Integer }

    public static Integer getRampPeriod() { (System.properties.getProperty('rampPeriod') ?: '10') as Integer }

    public static Integer getScenarioUserCount() { (System.properties.getProperty('scenarioUserCount') ?: '10') as Integer }

    public static String[] getDictionaryWords() { new File(DICTIONARY_FILE_PATH).readLines() as String[] }

    public static String getTextCorpus() { new File(CORPUS_FILE_PATH).withReader { it.getText() }}

    public static String[] getWordsDistro() { textCorpus.split() }

    public static String getObjectDataAsJson(String path) { ['curl', '-XGET', '-k', '-utestAdmin1:password', baseUrl + path].execute().text }
}
