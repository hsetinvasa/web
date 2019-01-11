package y.context;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DevicePlatform;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * replace
 * {@link org.springframework.mobile.device.DeviceWebArgumentResolver DeviceWebArgumentResolver}
 * for testing. Sufix url with &quot;device=mobile&quot; to simulate mobile
 * device.<br>
 * 替代
 * {@link org.springframework.mobile.device.DeviceWebArgumentResolver DeviceWebArgumentResolver}
 * 可作測試用，url串接可模擬手持裝置連線
 *
 * @author Kent Yeh
 */
@Log4j2
public class DeviceWebArgumentResolver implements WebArgumentResolver, InitializingBean {

    static final MobileDevice mobileDevice = new MobileDevice();
    @Getter
    @Setter
    private String requestName = "device";
    @Getter
    @Setter
    private String mobileValue = "mobile";

    public DeviceWebArgumentResolver() {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!StringUtils.hasText(requestName)) {
            requestName = "device";
        }
        if (!StringUtils.hasText(mobileValue)) {
            mobileValue = "mobile";
        }
    }

    @Override
    public Object resolveArgument(MethodParameter param, NativeWebRequest request) throws Exception {
        for (Map.Entry entry : request.getParameterMap().entrySet()) {
            log.info("parameter[\"{}\"]={}", entry.getKey(), entry.getValue());
        }
        if (Device.class.isAssignableFrom(param.getParameterType())) {
            if (mobileValue.equals(request.getParameter(requestName))) {
                return mobileDevice;
            } else {
                return DeviceUtils.getCurrentDevice(request);
            }
        } else {
            return WebArgumentResolver.UNRESOLVED;
        }
    }

    static class MobileDevice implements Device {

        @Override
        public boolean isMobile() {
            return true;
        }

        @Override
        public boolean isNormal() {
            return false;
        }

        @Override
        public boolean isTablet() {
            return false;
        }

        @Override
        public DevicePlatform getDevicePlatform() {
            return DevicePlatform.ANDROID;
        }
    }
}
