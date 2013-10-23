 ------
 XML Parsing and Printing
 ------
 Yegor Bugayenko
 ------
 2013-10-20
 ------

~~
~~ Copyright (c) 2012-2013, JCabi.com
~~ All rights reserved.
~~
~~ Redistribution and use in source and binary forms, with or without
~~ modification, are permitted provided that the following conditions
~~ are met: 1) Redistributions of source code must retain the above
~~ copyright notice, this list of conditions and the following
~~ disclaimer. 2) Redistributions in binary form must reproduce the above
~~ copyright notice, this list of conditions and the following
~~ disclaimer in the documentation and/or other materials provided
~~ with the distribution. 3) Neither the name of the jcabi.com nor
~~ the names of its contributors may be used to endorse or promote
~~ products derived from this software without specific prior written
~~ permission.
~~
~~ THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
~~ "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
~~ NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
~~ FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
~~ THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
~~ INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
~~ (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
~~ SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
~~ HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
~~ STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
~~ ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
~~ OF THE POSSIBILITY OF SUCH DAMAGE.
~~

XML Parsing and Printing

  Set of classes in
  {{{./apidocs-${project.version}/com/jcabi/xml/index.html}<<<com.jcabi.xml>>>}}
  is an extra layer on top of DOM that allows simple
  parsing, printing, and transforming of XML documents and nodes:

+--
public class Main {
  public static void main(String[] args) {
    XML xml = new XMLDocument("<orders><order id="4">Coffee to go</order></orders>");
    String id = xml.xpath("//order/@id").get(0);
    String name = xml.xpath("//order[@id=4]/text()");
    System.out.println(xml.toString());
  }
}
+--

  The only dependency you need is
  (you can also download
  {{{http://repo1.maven.org/maven2/com/jcabi/jcabi-xml/${project.version}/jcabi-xml-${project.version}.jar}<<<jcabi-xml-${project.version}.jar>>>}}
  and add it to the classpath):

+--
<dependency>
  <groupId>com.jcabi</groupId>
  <artifactId>jcabi-xml</artifactId>
  <version>${project.version}</version>
</dependency>
+--

* Cutting Edge Version

  If you want to use current version of the product, you can do it with
  this configuration in your <<<pom.xml>>>:

+--
<repositories>
  <repository>
    <id>oss.sonatype.org</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
  </repository>
</repositories>
<dependencies>
  <dependency>
    <groupId>com.jcabi</groupId>
    <artifactId>jcabi-xml</artifactId>
    <version>1.0-SNAPSHOT</version>
  </dependency>
</dependencies>
+--
