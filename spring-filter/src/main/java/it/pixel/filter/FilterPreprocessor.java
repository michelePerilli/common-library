package it.pixel.filter;

import it.pixel.filter.annotation.DisableFilters;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static it.pixel.filter.FilterManager.Status.DISABLED;
import static it.pixel.filter.FilterManager.Status.ENABLED;


@Aspect
@Component
@ConditionalOnProperty(prefix = "it.pixel.spring", name = "enable_filter", havingValue = "true")
public class FilterPreprocessor {

    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..)))")
    public Object applyFilters(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature methodSignature = joinPoint.getSignature();
        Class<?> classType = methodSignature.getDeclaringType();
        Method method = getMethod(joinPoint);

        boolean filterable = !classType.isAnnotationPresent(DisableFilters.class) && !method.isAnnotationPresent(DisableFilters.class);

        FilterManager manager = FilterManager.getInstance();

        if (filterable) {
            manager.setFiltersStatus(ENABLED);
        } else {
            manager.setFiltersStatus(DISABLED);
        }

        return joinPoint.proceed();
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        Class<?> classType = joinPoint.getSignature().getDeclaringType();

        List<Method> homonymMethods = Arrays.stream(classType.getMethods())
                .filter(x -> x.getName().equals(joinPoint.getSignature().getName())).toList();

        // se non ci sono overload sul metodo, allora lo ritorno poiché identificabile anche solo tramite il nome
        if (homonymMethods.size() == 1) {
            return homonymMethods.get(0);
        }

        // se invece ci sono overload, devo assicurarmi di prendere il metodo giusto controllando i parametri
        Object[] params = joinPoint.getArgs();
        int nParams = params.length;
        Class<?>[] paramsTypes;

        List<Method> sameNumberParametersMethods = homonymMethods.stream()
                .filter(x -> x.getParameterTypes().length == nParams).toList();

        if (sameNumberParametersMethods.size() == 1) {
            return sameNumberParametersMethods.get(0);
        }


        // a questo punto siamo nella situazione in cui abbiamo piu' metodi con stesso nome, stesso numero di parametri,
        // ma di tipi diversi, quindi procedo a controllarli

        paramsTypes = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                paramsTypes[i] = null;
            } else {
                paramsTypes[i] = params[i].getClass();
            }
        }

        for (Method method : sameNumberParametersMethods) {
            boolean same = true;
            Class<?>[] currentMethodParameterTypes = method.getParameterTypes();
            for (int i = 0; i < nParams; i++) {
                if (paramsTypes[i] != null && !currentMethodParameterTypes[i].equals(paramsTypes[i])) {
                    same = false;
                    break;
                }
            }
            if (same) {
                return method;
            }
        }
        throw new NoSuchMethodException("No valid method found");
    }


}
