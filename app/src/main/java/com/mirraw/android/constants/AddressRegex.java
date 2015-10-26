package com.mirraw.android.constants;

/**
 * Created by pavitra on 27/8/15.
 */
public class AddressRegex {

    /*# String with alphabates and numbers
    validates :name, format: { with:
    TODO:name RUBY REGEX: /\A[^\s][A-z0-9\s]+[^\s]\z/
    TODO:name JAVA REGEX:    \A[^\s][A-z0-9\s]+[^\s]\z
    }

    # String that has only alphabates and spaces
    validates :city, format: { with:
    TODO:city RUBY REGEX: /\A[^\s][A-z\s]+[^\s]\z/
    TODO:city JAVA REGEX: \A[^\s][A-z\s]+[^\s]\z
    }

    # State and Country will contain any string with alphabates
    # and spacial characters ''', '-', '[', ']', '?', '(', ')', '.', ',', '&'
    validates :state, :country, format: { with:
    TODO:state, :country RUBY REGEX: /\A[^\s][A-z\s\'\-\[\]\?\(\)\.\,\&]+[^\s]\z/
    TODO:state, :country JAVA REGEX: \A[^\s][A-z\s\'\-\[\]\?\(\)\.\,\&]+[^\s]\z
    }

        # Formats to be allowed are +(123) 123 123 12, +12 123 123123, 12331213213
        validates :phone, length: { minimum: 8 },
        format: { with:
        TODO:phone RUBY REGEX: /\A\+?\(?[A-z0-9]{1,3}\)?[0-9\s]+\z/
        TODO:phone JAVA REGEX: \A\+?\(?[A-z0-9]{1,3}\)?[0-9\s]+\z
        }

        # Formats to be allowed are 442402, 44240-5555, G3H 6A3
        validates :pincode, format: { with:
        TODO:pincode RUBY REGEX: /\A[^\s-][A-z\d\-\s]+\z/
        TODO:pincode JAVA REGEX: \A[^\s-][A-z\d\-\s]+\z
        }

        # Alphabates, numbers and special characters
        # ''', '-', '[', ']', '?', '(', ')', '.', ',', '&'
        validates :street_address, length: { maximum: 255 },
        format: { with:
        TODO:street_address RUBY REGEX: /\A[^\s][A-z\d\s\'\-\[\]\?\(\)\.\,\&]+[^\s]\z/
        TODO:street_address JAVA REGEX: \A[^\s][A-z\d\s\'\-\[\]\?\(\)\.\,\&]+[^\s]\z

        TODO: street_address New Ruby Regex: /\A[^\s][A-z\d\s\'\-\[\]\?\(\)\.\,\&\/\#]+[^\s]\z/
        TODO:street_address New JAVA REGEX: \A[^\s][A-z\d\s\'\-\[\]\?\(\)\.\,\&\/\#]+[^\s]\z

        TODO: street_address New Ruby Regex: /\A[^\s][A-z\d\s\'\-\[\]\?\(\)\.\,\&\/]+[^\s]\z/
        TODO:street_address New JAVA REGEX: \A[^\s][A-z\d\s\'\-\[\]\?\(\)\.\,\&\/]+[^\s]\z
        }*/

    public static final String NAME = "\\A[^\\s][A-z0-9\\s]+[^\\s]\\z";
    public static final String COUNTRY = "\\A[^\\s][A-z\\s\\'\\-\\[\\]\\?\\(\\)\\.\\,\\&]+[^\\s]\\z";
    public static final String STATE = "\\A[^\\s][A-z\\s\\'\\-\\[\\]\\?\\(\\)\\.\\,\\&]+[^\\s]\\z";
    public static final String CITY = "\\A[^\\s][A-z\\s]+[^\\s]\\z";
    public static final String PINCODE = "\\A[^\\s-][A-z\\d\\-\\s]+\\z";
    //public static final String STREET = "\\A[^\\s][A-z\\d\\s\\'\\-\\[\\]\\?\\(\\)\\.\\,\\&]+[^\\s]\\z";          //Maximum length 255
    //public static final String STREET = "\\A[^\\s][A-z\\d\\s\\'\\-\\[\\]\\?\\(\\)\\.\\,\\&\\/]+[^\\s]\\z";
    public static final String STREET = "\\A[^\\s][A-z\\d\\s\\'\\-\\[\\]\\?\\(\\)\\.\\,\\&\\/\\#]+[^\\s]\\z";
    public static final String MOBILE = "\\A\\+?\\(?[A-z0-9]{1,3}\\)?[0-9\\s]+\\z";         //Minimum length 8
}
