#Setting up Eclipse project

# Eclipse Setup #

**1. SVN checkout**
[See here.](http://code.google.com/p/taglocate/source/checkout)

**2. Import projects**
Make sure to have both the TagLocate as well as the NfcMimeLib project added to your workspace. You may need to add NfcMimeLib manually to TagLocate as Android project library: This can be done in the 'Android' part of the project properties under 'Libraries'.

**3. Dependencies**
I'm planning to use Maven at some point, but for now, you'll need to add the following jars manually to the build path:

  * [Apache Commons Codec](http://commons.apache.org/codec/download_codec.cgi)
  * [Apache Commons IO](http://commons.apache.org/io/download_io.cgi)
  * [Apache Commons Lang](http://commons.apache.org/lang/download_lang.cgi)
  * [Guice 2.0 (no AOP version)](http://code.google.com/p/google-guice/downloads/detail?name=guice-2.0-no_aop.jar&can=2&q=)
  * [Roboguice (1.1.2)](http://repo1.maven.org/maven2/org/roboguice/roboguice/1.1.2/)
  * [Simple XML](http://simple.sourceforge.net/download.php)