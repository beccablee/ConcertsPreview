
package com.example.jinjinz.concertprev.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import org.parceler.Generated;
import org.parceler.InjectionUtil;
import org.parceler.ParcelWrapper;
import org.parceler.ParcelerRuntimeException;

@Generated(value = "org.parceler.ParcelAnnotationProcessor", date = "2016-07-13T10:31-0700")
@SuppressWarnings({
    "unchecked",
    "deprecation"
})
public class Concert$$Parcelable
    implements Parcelable, ParcelWrapper<com.example.jinjinz.concertprev.models.Concert>
{

    private com.example.jinjinz.concertprev.models.Concert concert$$0;
    @SuppressWarnings("UnusedDeclaration")
    public final static Concert$$Parcelable.Creator$$0 CREATOR = new Concert$$Parcelable.Creator$$0();

    public Concert$$Parcelable(com.example.jinjinz.concertprev.models.Concert concert$$2) {
        concert$$0 = concert$$2;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel$$0, int flags) {
        write(concert$$0, parcel$$0, flags, new HashSet<Integer>());
    }

    public static void write(com.example.jinjinz.concertprev.models.Concert concert$$1, android.os.Parcel parcel$$1, int flags$$0, Set<Integer> identitySet$$0) {
        int identity$$0 = System.identityHashCode(concert$$1);
        parcel$$1 .writeInt(identity$$0);
        if (!identitySet$$0 .contains(identity$$0)) {
            identitySet$$0 .add(identity$$0);
            if (concert$$1 == null) {
                parcel$$1 .writeInt(-1);
            } else {
                parcel$$1 .writeInt(1);
                parcel$$1 .writeString(InjectionUtil.getField(java.lang.String.class, com.example.jinjinz.concertprev.models.Concert.class, concert$$1, "venue"));
                parcel$$1 .writeString(InjectionUtil.getField(java.lang.String.class, com.example.jinjinz.concertprev.models.Concert.class, concert$$1, "backdropImage"));
                if (InjectionUtil.getField(java.util.ArrayList.class, com.example.jinjinz.concertprev.models.Concert.class, concert$$1, "artists") == null) {
                    parcel$$1 .writeInt(-1);
                } else {
                    parcel$$1 .writeInt(InjectionUtil.getField(java.util.ArrayList.class, com.example.jinjinz.concertprev.models.Concert.class, concert$$1, "artists").size());
                    for (java.lang.String string$$0 : ((java.util.ArrayList<java.lang.String> ) InjectionUtil.getField(java.util.ArrayList.class, com.example.jinjinz.concertprev.models.Concert.class, concert$$1, "artists"))) {
                        parcel$$1 .writeString(string$$0);
                    }
                }
                parcel$$1 .writeString(InjectionUtil.getField(java.lang.String.class, com.example.jinjinz.concertprev.models.Concert.class, concert$$1, "city"));
                parcel$$1 .writeString(InjectionUtil.getField(java.lang.String.class, com.example.jinjinz.concertprev.models.Concert.class, concert$$1, "countryCode"));
                parcel$$1 .writeString(InjectionUtil.getField(java.lang.String.class, com.example.jinjinz.concertprev.models.Concert.class, concert$$1, "eventTime"));
                parcel$$1 .writeString(InjectionUtil.getField(java.lang.String.class, com.example.jinjinz.concertprev.models.Concert.class, concert$$1, "eventName"));
                parcel$$1 .writeString(InjectionUtil.getField(java.lang.String.class, com.example.jinjinz.concertprev.models.Concert.class, concert$$1, "stateCode"));
                parcel$$1 .writeString(InjectionUtil.getField(java.lang.String.class, com.example.jinjinz.concertprev.models.Concert.class, concert$$1, "eventDate"));
            }
        }
    }

    @Override
    public int describeContents() {
        return  0;
    }

    @Override
    public com.example.jinjinz.concertprev.models.Concert getParcel() {
        return concert$$0;
    }

    public static com.example.jinjinz.concertprev.models.Concert read(android.os.Parcel parcel$$3, Map<Integer, Object> identityMap$$0) {
        com.example.jinjinz.concertprev.models.Concert concert$$3;
        int identity$$1 = parcel$$3 .readInt();
        if (identityMap$$0 .containsKey(identity$$1)) {
            com.example.jinjinz.concertprev.models.Concert concert$$4 = ((com.example.jinjinz.concertprev.models.Concert) identityMap$$0 .get(identity$$1));
            if ((concert$$4 == null)&&(identity$$1 != 0)) {
                throw new ParcelerRuntimeException("An instance loop was detected whild building Parcelable and deseralization cannot continue.  This error is most likely due to using @ParcelConstructor or @ParcelFactory.");
            }
            return concert$$4;
        }
        if (parcel$$3 .readInt() == -1) {
            concert$$3 = null;
            identityMap$$0 .put(identity$$1, null);
        } else {
            com.example.jinjinz.concertprev.models.Concert concert$$5;
            identityMap$$0 .put(identity$$1, null);
            concert$$5 = new com.example.jinjinz.concertprev.models.Concert();
            identityMap$$0 .put(identity$$1, concert$$5);
            InjectionUtil.setField(com.example.jinjinz.concertprev.models.Concert.class, concert$$5, "venue", parcel$$3 .readString());
            InjectionUtil.setField(com.example.jinjinz.concertprev.models.Concert.class, concert$$5, "backdropImage", parcel$$3 .readString());
            int int$$0 = parcel$$3 .readInt();
            java.util.ArrayList<java.lang.String> list$$0;
            if (int$$0 < 0) {
                list$$0 = null;
            } else {
                list$$0 = new java.util.ArrayList<java.lang.String>(int$$0);
                for (int int$$1 = 0; (int$$1 <int$$0); int$$1 ++) {
                    list$$0 .add(parcel$$3 .readString());
                }
            }
            InjectionUtil.setField(com.example.jinjinz.concertprev.models.Concert.class, concert$$5, "artists", list$$0);
            InjectionUtil.setField(com.example.jinjinz.concertprev.models.Concert.class, concert$$5, "city", parcel$$3 .readString());
            InjectionUtil.setField(com.example.jinjinz.concertprev.models.Concert.class, concert$$5, "countryCode", parcel$$3 .readString());
            InjectionUtil.setField(com.example.jinjinz.concertprev.models.Concert.class, concert$$5, "eventTime", parcel$$3 .readString());
            InjectionUtil.setField(com.example.jinjinz.concertprev.models.Concert.class, concert$$5, "eventName", parcel$$3 .readString());
            InjectionUtil.setField(com.example.jinjinz.concertprev.models.Concert.class, concert$$5, "stateCode", parcel$$3 .readString());
            InjectionUtil.setField(com.example.jinjinz.concertprev.models.Concert.class, concert$$5, "eventDate", parcel$$3 .readString());
            concert$$3 = concert$$5;
        }
        return concert$$3;
    }

    public final static class Creator$$0
        implements Creator<Concert$$Parcelable>
    {


        @Override
        public Concert$$Parcelable createFromParcel(android.os.Parcel parcel$$2) {
            return new Concert$$Parcelable(read(parcel$$2, new HashMap<Integer, Object>()));
        }

        @Override
        public Concert$$Parcelable[] newArray(int size) {
            return new Concert$$Parcelable[size] ;
        }

    }

}
