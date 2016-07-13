
package org.parceler;

import java.util.HashMap;
import java.util.Map;
import com.example.jinjinz.concertprev.models.Concert;
import com.example.jinjinz.concertprev.models.Concert$$Parcelable;

@Generated(value = "org.parceler.ParcelAnnotationProcessor", date = "2016-07-13T10:31-0700")
@SuppressWarnings({
    "unchecked",
    "deprecation"
})
public class Parceler$$Parcels
    implements Repository<org.parceler.Parcels.ParcelableFactory>
{

    private final Map<Class, org.parceler.Parcels.ParcelableFactory> map$$0 = new HashMap<Class, org.parceler.Parcels.ParcelableFactory>();

    public Parceler$$Parcels() {
        map$$0 .put(Concert.class, new Parceler$$Parcels.Concert$$Parcelable$$0());
    }

    public Map<Class, org.parceler.Parcels.ParcelableFactory> get() {
        return map$$0;
    }

    private final static class Concert$$Parcelable$$0
        implements org.parceler.Parcels.ParcelableFactory<Concert>
    {


        @Override
        public Concert$$Parcelable buildParcelable(Concert input) {
            return new Concert$$Parcelable(input);
        }

    }

}
