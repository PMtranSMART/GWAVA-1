# Genome-Wide Association Visual Analyzer for tranSMART
*This is an instruction of how to generate a gwava war file to work with 17.1 version of tranSMART.
It relates to https://github.com/thehyve/transmart-core/ repository.*

* [Required libraries](#libraries)
* [Configuration](#configuration)
* [Generating a war file](#generating-war-file)
* [Test data](#test-data)

## <a name="libraries"></a> 1. Required libraries
The following libraries must be present in */lib* folder:
* commons-beanutils-1.8.3.jar
* commons-beanutils-bean-collections-1.8.3.jar
* commons-beanutils-core-1.8.3.jar
* commons-codec-1.6.jar
* commons-digester3-3.2.jar
* commons-digester3-3.2-javadoc.jar
* commons-lang3-3.1.jar
* commons-logging-1.1.1.jar
* httpclient-4.0.jar 
* httpcore-4.2.1.jar
* itextpdf-5.3.4.jar
* jdom-2.0.6.jar
* jersey-client-1.4.jar
* jersey-core-1.4.jar
* jgoodies-common-1.3.1.jar
* jgoodies-looks-2.5.1.jar
* log4j-1.2.17.jar
* log4j-api-2.5.jar
* log4j-core-2.5.jar

## <a name="configuration"></a> 2. Configuration

### Settings required for tranSMART configuration file:

    def gwavaEnabled      = true
    ...
    /* {{{ Spring Security configuration */    
    grails { plugin { springsecurity {
        ...
        if (useRequestMap) { ... }
        else {
            def gwavaMappings = [
                 [pattern: '/gwasWeb/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
            ]
            ...
            interceptUrlMap = [ ... ] +
            (gwavaEnabled ?  gwavaMappings : []) +
            [
                [pattern: '/**', access: ['IS_AUTHENTICATED_REMEMBERED']], // must be last
            ]
            ...
         }
         ...
    }}}                
    ...
    /* {{{ gwava */
    if (org.transmartproject.app.gwavaEnabled) {
        // assume deployment alongside transmart
        com { recomdata { rwg { 
            webstart {
                def url       = new URL(org.transmartproject.app.transmartURL)
                codebase      = "$url.protocol://$url.host${url.port != -1 ? ":$url.port" : ''}/gwava"
                jar           = './ManhattanViz2.1k.jar'
                mainClass     = 'com.pfizer.mrbt.genomics.Driver'
                gwavaInstance = 'transmartstage'
                transmart.url = org.transmartproject.app.transmartURL - ~'\\/$'
           } 
           qqplots {
               cacheImages = jobsDirectory + '/cachedQQplotImages/'
               temporaryImageFolder = '/images/tempImages/'
               temporaryImageFolderFullPath = explodedWarDir + temporaryImageFolder
           }
           manhattanplots {
               cacheImages = jobsDirectory + '/cachedManhattanplotImages/'
               temporaryImageFolder = '/images/tempImages/'
               temporaryImageFolderFullPath = explodedWarDir + temporaryImageFolder
           }
       } } }
    }
    ...

### Settings for build.xml: 
* Line 44: *webstart.root* property - should contain the URL of where the Gwava part will be deployed. It is already configured for the test instances and so is the war file. 
* Line 87: *keystore.* properties - keystore details go at line.


## <a name="generating-war-file"></a> 3. Generating a war file
*IMPORTANT! Oracle Java 7 Web Start (default webStartProvider for Linux) is working only when war is built using JAVA7*

Run:
    
    ant transmartwar 

War file will be created inside */dist* folder.

## <a name="test-data"></a> 4. Test data
17.1 tranSMART test data contains a study called MAGIC, however, it is not a part of basic transmart-data loading scripts. It can be loaded using transmart-batch.
Check https://github.com/thehyve/transmart-core/tree/master/transmart-batch/ for more detailed instructions.
* to load MAGIC study with transmart-batch:


    ./transmart-batch.sh -p studies/MAGIC/gwas.params
    
* to load data for *de_rc_snp_info* table with transmart-batch:


    gradle functionalTestPrepare

