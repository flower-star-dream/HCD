package top.flowerstardream.hcd.base.framework.statemachine;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 通用状态机框架
 * 支持状态流转验证、前置条件检查、后置动作执行
 *
 * @author huahai
 */
public class StateMachine<S extends Enum<S>, E extends Enum<E>, C> {

    private final Map<S, Map<E, Transition<S, E, C>>> transitions = new ConcurrentHashMap<>();
    private final List<StateChangeListener<S, E, C>> listeners = new ArrayList<>();
    private final Map<S, List<Consumer<C>>> enterActions = new ConcurrentHashMap<>();
    private final Map<S, List<Consumer<C>>> exitActions = new ConcurrentHashMap<>();

    /**
     * 状态转换定义
     */
    public static class Transition<S, E, C> {
        @Getter
        private final S source;
        @Getter
        private final S target;
        @Getter
        private final E event;
        private final List<TransitionCondition<C>> conditions;
        private final List<TransitionAction<C>> actions;

        public Transition(S source, S target, E event) {
            this.source = source;
            this.target = target;
            this.event = event;
            this.conditions = new ArrayList<>();
            this.actions = new ArrayList<>();
        }

        public Transition<S, E, C> when(TransitionCondition<C> condition) {
            this.conditions.add(condition);
            return this;
        }

        public Transition<S, E, C> perform(TransitionAction<C> action) {
            this.actions.add(action);
            return this;
        }

        public boolean canExecute(C context) {
            return conditions.stream().allMatch(condition -> condition.test(context));
        }

        public void executeActions(C context) {
            actions.forEach(action -> action.execute(context));
        }

    }

    /**
     * 状态转换条件
     */
    @FunctionalInterface
    public interface TransitionCondition<C> {
        boolean test(C context);
    }

    /**
     * 状态转换动作
     */
    @FunctionalInterface
    public interface TransitionAction<C> {
        void execute(C context);
    }

    /**
     * 状态变化监听器
     */
    @FunctionalInterface
    public interface StateChangeListener<S, E, C> {
        void onStateChanged(S oldState, S newState, E event, C context);
    }

    /**
     * 状态机构建器
     */
    public static class Builder<S extends Enum<S>, E extends Enum<E>, C> {
        private final StateMachine<S, E, C> stateMachine;

        public Builder() {
            this.stateMachine = new StateMachine<>();
        }

        public Builder<S, E, C> addTransition(S source, S target, E event) {
            Transition<S, E, C> transition = new Transition<>(source, target, event);
            stateMachine.transitions
                .computeIfAbsent(source, k -> new HashMap<>())
                .put(event, transition);
            return this;
        }

        public Builder<S, E, C> addTransition(Transition<S, E, C> transition) {
            stateMachine.transitions
                .computeIfAbsent(transition.getSource(), k -> new HashMap<>())
                .put(transition.getEvent(), transition);
            return this;
        }

        public Builder<S, E, C> addStateChangeListener(StateChangeListener<S, E, C> listener) {
            stateMachine.listeners.add(listener);
            return this;
        }

        public Builder<S, E, C> addEnterAction(S state, Consumer<C> action) {
            stateMachine.enterActions.computeIfAbsent(state, k -> new ArrayList<>()).add(action);
            return this;
        }

        public Builder<S, E, C> addExitAction(S state, Consumer<C> action) {
            stateMachine.exitActions.computeIfAbsent(state, k -> new ArrayList<>()).add(action);
            return this;
        }

        public StateMachine<S, E, C> build() {
            return stateMachine;
        }
    }

    /**
     * 触发状态转换
     */
    public boolean fireEvent(S currentState, E event, C context) {
        Map<E, Transition<S, E, C>> eventTransitions = transitions.get(currentState);
        if (eventTransitions == null) {
            return false;
        }

        Transition<S, E, C> transition = eventTransitions.get(event);
        if (transition == null) {
            return false;
        }

        // 检查条件
        if (!transition.canExecute(context)) {
            return false;
        }

        // 执行退出动作
        List<Consumer<C>> exitActionsList = exitActions.get(currentState);
        if (exitActionsList != null) {
            exitActionsList.forEach(action -> action.accept(context));
        }

        // 执行转换动作
        transition.executeActions(context);

        // 执行进入动作
        List<Consumer<C>> enterActionsList = enterActions.get(transition.getTarget());
        if (enterActionsList != null) {
            enterActionsList.forEach(action -> action.accept(context));
        }

        // 通知监听器
        listeners.forEach(listener ->
            listener.onStateChanged(currentState, transition.getTarget(), event, context));

        return true;
    }

    /**
     * 获取可能的状态转换
     */
    public Set<S> getPossibleTransitions(S currentState, E event) {
        Map<E, Transition<S, E, C>> eventTransitions = transitions.get(currentState);
        if (eventTransitions == null) {
            return Collections.emptySet();
        }

        Transition<S, E, C> transition = eventTransitions.get(event);
        if (transition == null) {
            return Collections.emptySet();
        }

        return Collections.singleton(transition.getTarget());
    }

    /**
     * 验证状态转换是否允许
     */
    public boolean canTransition(S currentState, E event, C context) {
        Map<E, Transition<S, E, C>> eventTransitions = transitions.get(currentState);
        if (eventTransitions == null) {
            return false;
        }

        Transition<S, E, C> transition = eventTransitions.get(event);
        if (transition == null) {
            return false;
        }

        return transition.canExecute(context);
    }
}