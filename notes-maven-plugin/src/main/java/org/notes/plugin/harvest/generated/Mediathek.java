//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.04.13 at 07:58:21 PM CEST 
//


package org.notes.plugin.harvest.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Filmliste">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Filmliste-Datum" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Filmliste-Datum-GMT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Filmliste-Version" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *                   &lt;element name="Filmliste-Programm" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Feldinfo">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="a" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="b" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="c" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="d" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="v" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="w" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="e" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="f" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="m" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="t" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="n" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="bb" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="g" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="k" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="l" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="o" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="i" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="j" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="r" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="s" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="u" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="aa" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="y" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="z" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="X" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="b" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="c" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="d" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="e" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="f" type="{http://www.w3.org/2001/XMLSchema}time"/>
 *                   &lt;element name="m" type="{http://www.w3.org/2001/XMLSchema}time"/>
 *                   &lt;element name="t" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *                   &lt;element name="n" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="g" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                   &lt;element name="k" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                   &lt;element name="o" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                   &lt;element name="r" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="y" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "filmliste",
        "feldinfo",
        "x"
})
@XmlRootElement(name = "Mediathek")
public class Mediathek {

    @XmlElement(name = "Filmliste", required = true)
    protected Mediathek.Filmliste filmliste;
    @XmlElement(name = "Feldinfo", required = true)
    protected Mediathek.Feldinfo feldinfo;
    @XmlElement(name = "X")
    protected List<Mediathek.X> x;

    /**
     * Gets the value of the filmliste property.
     *
     * @return possible object is
     * {@link Mediathek.Filmliste }
     */
    public Mediathek.Filmliste getFilmliste() {
        return filmliste;
    }

    /**
     * Sets the value of the filmliste property.
     *
     * @param value allowed object is
     *              {@link Mediathek.Filmliste }
     */
    public void setFilmliste(Mediathek.Filmliste value) {
        this.filmliste = value;
    }

    /**
     * Gets the value of the feldinfo property.
     *
     * @return possible object is
     * {@link Mediathek.Feldinfo }
     */
    public Mediathek.Feldinfo getFeldinfo() {
        return feldinfo;
    }

    /**
     * Sets the value of the feldinfo property.
     *
     * @param value allowed object is
     *              {@link Mediathek.Feldinfo }
     */
    public void setFeldinfo(Mediathek.Feldinfo value) {
        this.feldinfo = value;
    }

    /**
     * Gets the value of the x property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the x property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getX().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Mediathek.X }
     */
    public List<Mediathek.X> getX() {
        if (x == null) {
            x = new ArrayList<Mediathek.X>();
        }
        return this.x;
    }


    /**
     * <p>Java class for anonymous complex type.
     * <p/>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p/>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;choice maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="a" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="b" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="c" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="d" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="v" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="w" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="e" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="f" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="m" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="t" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="n" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="bb" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="g" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="k" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="l" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="o" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="i" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="j" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="r" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="s" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="u" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="aa" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="y" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="z" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "aOrBOrC"
    })
    public static class Feldinfo {

        @XmlElementRefs({
                @XmlElementRef(name = "s", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "l", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "b", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "o", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "u", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "i", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "n", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "r", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "c", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "d", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "k", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "e", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "f", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "m", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "z", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "y", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "aa", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "bb", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "g", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "a", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "t", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "v", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "w", type = JAXBElement.class, required = false),
                @XmlElementRef(name = "j", type = JAXBElement.class, required = false)
        })
        protected List<JAXBElement<String>> aOrBOrC;

        /**
         * Gets the value of the aOrBOrC property.
         * <p/>
         * <p/>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the aOrBOrC property.
         * <p/>
         * <p/>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAOrBOrC().add(newItem);
         * </pre>
         * <p/>
         * <p/>
         * <p/>
         * Objects of the following type(s) are allowed in the list
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         * {@link JAXBElement }{@code <}{@link String }{@code >}
         */
        public List<JAXBElement<String>> getAOrBOrC() {
            if (aOrBOrC == null) {
                aOrBOrC = new ArrayList<JAXBElement<String>>();
            }
            return this.aOrBOrC;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * <p/>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p/>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Filmliste-Datum" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Filmliste-Datum-GMT" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Filmliste-Version" type="{http://www.w3.org/2001/XMLSchema}byte"/>
     *         &lt;element name="Filmliste-Programm" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "filmlisteDatum",
            "filmlisteDatumGMT",
            "filmlisteVersion",
            "filmlisteProgramm"
    })
    public static class Filmliste {

        @XmlElement(name = "Filmliste-Datum", required = true)
        protected String filmlisteDatum;
        @XmlElement(name = "Filmliste-Datum-GMT", required = true)
        protected String filmlisteDatumGMT;
        @XmlElement(name = "Filmliste-Version")
        protected byte filmlisteVersion;
        @XmlElement(name = "Filmliste-Programm", required = true)
        protected String filmlisteProgramm;

        /**
         * Gets the value of the filmlisteDatum property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getFilmlisteDatum() {
            return filmlisteDatum;
        }

        /**
         * Sets the value of the filmlisteDatum property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setFilmlisteDatum(String value) {
            this.filmlisteDatum = value;
        }

        /**
         * Gets the value of the filmlisteDatumGMT property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getFilmlisteDatumGMT() {
            return filmlisteDatumGMT;
        }

        /**
         * Sets the value of the filmlisteDatumGMT property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setFilmlisteDatumGMT(String value) {
            this.filmlisteDatumGMT = value;
        }

        /**
         * Gets the value of the filmlisteVersion property.
         */
        public byte getFilmlisteVersion() {
            return filmlisteVersion;
        }

        /**
         * Sets the value of the filmlisteVersion property.
         */
        public void setFilmlisteVersion(byte value) {
            this.filmlisteVersion = value;
        }

        /**
         * Gets the value of the filmlisteProgramm property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getFilmlisteProgramm() {
            return filmlisteProgramm;
        }

        /**
         * Sets the value of the filmlisteProgramm property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setFilmlisteProgramm(String value) {
            this.filmlisteProgramm = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * <p/>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p/>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="b" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="c" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="d" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="e" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="f" type="{http://www.w3.org/2001/XMLSchema}time"/>
     *         &lt;element name="m" type="{http://www.w3.org/2001/XMLSchema}time"/>
     *         &lt;element name="t" type="{http://www.w3.org/2001/XMLSchema}short"/>
     *         &lt;element name="n" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="g" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *         &lt;element name="k" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *         &lt;element name="o" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *         &lt;element name="r" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="y" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "b",
            "c",
            "d",
            "e",
            "f",
            "m",
            "t",
            "n",
            "g",
            "k",
            "o",
            "r",
            "y"
    })
    public static class X {

        protected String b;
        protected String c;
        @XmlElement(required = true)
        protected String d;
        @XmlElement(required = true)
        protected String e;
        @XmlElement(required = true)
        @XmlSchemaType(name = "time")
        protected XMLGregorianCalendar f;
        @XmlElement(required = true)
        @XmlSchemaType(name = "time")
        protected XMLGregorianCalendar m;
        protected short t;
        @XmlElement(required = true)
        protected String n;
        @XmlElement(required = true)
        @XmlSchemaType(name = "anyURI")
        protected String g;
        @XmlElement(required = true)
        @XmlSchemaType(name = "anyURI")
        protected String k;
        @XmlElement(required = true)
        @XmlSchemaType(name = "anyURI")
        protected String o;
        @XmlElement(required = true)
        protected String r;
        protected int y;

        /**
         * Gets the value of the b property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getB() {
            return b;
        }

        /**
         * Sets the value of the b property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setB(String value) {
            this.b = value;
        }

        /**
         * Gets the value of the c property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getC() {
            return c;
        }

        /**
         * Sets the value of the c property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setC(String value) {
            this.c = value;
        }

        /**
         * Gets the value of the d property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getD() {
            return d;
        }

        /**
         * Sets the value of the d property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setD(String value) {
            this.d = value;
        }

        /**
         * Gets the value of the e property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getE() {
            return e;
        }

        /**
         * Sets the value of the e property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setE(String value) {
            this.e = value;
        }

        /**
         * Gets the value of the f property.
         *
         * @return possible object is
         * {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getF() {
            return f;
        }

        /**
         * Sets the value of the f property.
         *
         * @param value allowed object is
         *              {@link XMLGregorianCalendar }
         */
        public void setF(XMLGregorianCalendar value) {
            this.f = value;
        }

        /**
         * Gets the value of the m property.
         *
         * @return possible object is
         * {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getM() {
            return m;
        }

        /**
         * Sets the value of the m property.
         *
         * @param value allowed object is
         *              {@link XMLGregorianCalendar }
         */
        public void setM(XMLGregorianCalendar value) {
            this.m = value;
        }

        /**
         * Gets the value of the t property.
         */
        public short getT() {
            return t;
        }

        /**
         * Sets the value of the t property.
         */
        public void setT(short value) {
            this.t = value;
        }

        /**
         * Gets the value of the n property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getN() {
            return n;
        }

        /**
         * Sets the value of the n property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setN(String value) {
            this.n = value;
        }

        /**
         * Gets the value of the g property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getG() {
            return g;
        }

        /**
         * Sets the value of the g property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setG(String value) {
            this.g = value;
        }

        /**
         * Gets the value of the k property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getK() {
            return k;
        }

        /**
         * Sets the value of the k property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setK(String value) {
            this.k = value;
        }

        /**
         * Gets the value of the o property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getO() {
            return o;
        }

        /**
         * Sets the value of the o property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setO(String value) {
            this.o = value;
        }

        /**
         * Gets the value of the r property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getR() {
            return r;
        }

        /**
         * Sets the value of the r property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setR(String value) {
            this.r = value;
        }

        /**
         * Gets the value of the y property.
         */
        public int getY() {
            return y;
        }

        /**
         * Sets the value of the y property.
         */
        public void setY(int value) {
            this.y = value;
        }


        public String getSender() {
            return b;
        }

        public String getTitel() {
            return d;
        }

        public String getDatum() {
            return e;
        }

        public XMLGregorianCalendar getZeit() {
            return f;
        }

        public XMLGregorianCalendar getDauer() {
            return m;
        }

        public String getBeschreibung() {
            return n;
        }

        public String getUrl() {
            return g;
        }

        public String getWebsite() {
            return k;
        }
//        <k>Website</k>
//        <l>Aboname</l>
//        <o>Bild</o>
//        <i>UrlRTMP</i>
//        <j>UrlAuth</j>
//        <r>Url_Klein</r>
//        <s>UrlRTMP_Klein</s>
//        <t>Url_HD</t>
//        <u>UrlRTMP_HD</u>
//        <aa>Url_History</aa>
//        <y>DatumL</y>
//        <z>Ref</z>


    }

}