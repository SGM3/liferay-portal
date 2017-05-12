CATALINA_OPTS="$CATALINA_OPTS -Dfile.encoding=UTF8 -Djava.net.preferIPv4Stack=true @java.security.config@ -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false -Duser.timezone=GMT -Xmx1024m -XX:MaxPermSize=384m"

if [ "" != "$JAVA_HOME" ]; then
	PATH="$JAVA_HOME/bin:$PATH"
fi

if [ "" != "$JRE_HOME" ]; then
	PATH="$JRE_HOME/bin:$PATH"
fi