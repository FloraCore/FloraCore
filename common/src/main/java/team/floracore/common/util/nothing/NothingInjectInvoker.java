package team.floracore.common.util.nothing;


import team.floracore.common.util.*;
import team.floracore.common.util.wrapper.*;

import java.lang.invoke.*;
import java.util.Optional;
import java.util.function.*;

public class NothingInjectInvoker implements Function<Object[], Object> {
	public MethodHandle target;
	public Class<? extends WrappedObject>[] argsWrappers;

	public NothingInjectInvoker(MethodHandle target, Class<? extends WrappedObject>... argsWrappers) {
		this.target = target;
		this.argsWrappers = argsWrappers;
	}

	@Override
	public Object apply(Object[] args) {
		try {
			Object[] as = new Object[args.length];
			for (int i = 0; i != args.length; i++) {
				if (argsWrappers[i] != null)
					as[i] = WrappedObject.wrap(argsWrappers[i], ((Object[]) args[i])[0]);
				else
					as[i] = args[i];
			}
			Object r = target.invokeWithArguments(as);
			for (int i = 0; i != args.length; i++) {
				if (argsWrappers[i] != null)
					((Object[]) args[i])[0] = ((WrappedObject) as[i]).getRaw();
			}
			if (r != null) {
				Object o = TypeUtil.<Optional<?>, Object>cast(r).orElse(null);
				if (o instanceof WrappedObject)
					r = Optional.ofNullable(TypeUtil.<WrappedObject, Object>cast(o).getRaw());
			}
			return r;
		} catch (Throwable e) {
			throw TypeUtil.throwException(e);
		}
	}
}
