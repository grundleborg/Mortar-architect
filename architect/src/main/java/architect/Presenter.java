package architect;

import android.content.Context;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;

import java.util.List;

import architect.view.HandlesViewTransition;
import mortar.MortarScope;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
class Presenter {

    private final Transitions transitions;
    private NavigatorView view;
    private Dispatcher.Callback dispatchingCallback;
    private boolean active;

    /**
     * Track the session
     * Each session lives during the lifespan of a navigation view instance
     * When a new view is provided, new unique session id is set
     *
     * id starts at 1
     * negative value means there is no session (like during config changes)
     */
    private int sessionId = 0;
//    private final List<History.Entry> entriesInView = new ArrayList<>();
//    private final List<MortarScope> scopesInView = new ArrayList<>();
//    private final List<String> presentedModalScopes = new ArrayList<>();

    Presenter(Transitions transitions) {
        this.transitions = transitions;
    }

    void newSession() {
        Preconditions.checkArgument(sessionId <= 0, "New session while session id is valid");
        sessionId *= -1;
        sessionId++;
    }

    void invalidateSession() {
        Preconditions.checkArgument(sessionId > 0, "Invalidate session while session id is invalid");
        sessionId *= -1;
    }

    void attach(NavigatorView view) {
        Preconditions.checkNotNull(view, "Cannot attach null navigator view");
        Preconditions.checkNull(this.view, "Current navigator view not null, did you forget to detach the previous view?");
        Preconditions.checkArgument(!active, "Navigator view must be inactive before attaching");
        Preconditions.checkNull(dispatchingCallback, "Dispatching callback must be null before attaching");

        newSession();
        this.view = view;
        this.view.sessionId = sessionId;
    }

    void detach() {
        Preconditions.checkNotNull(view, "Cannot detach null navigator view");
        Preconditions.checkArgument(!active, "Navigator view must be inactive before detaching");
        Preconditions.checkNull(dispatchingCallback, "Dispatching callback must be null before detaching");

        invalidateSession();
        view = null;
    }

    void activate() {
        Preconditions.checkNotNull(view, "Navigator view cannot be null, did you forget to attach?");
        Preconditions.checkNull(dispatchingCallback, "Dispatching callback must be null before activating");
        active = true;

    }

    void desactivate() {
        Preconditions.checkNotNull(view, "Navigator view cannot be null, did you forget to attach?");
        active = false;

        completeDispatchingCallback();
    }

    void restore(List<Dispatcher.DispatchEntry> dispatchEntries) {
        Preconditions.checkNotNull(view, "Container view cannot be null");
        Preconditions.checkArgument(view.getChildCount() == 0, "Restore requires view with no children");
        if (dispatchEntries.isEmpty()) {
            return;
        }

        Dispatcher.DispatchEntry dispatchEntry;
        View child;
        for (int i = 0; i < dispatchEntries.size(); i++) {
            dispatchEntry = dispatchEntries.get(i);
            Logger.d("Restore: %s", dispatchEntry.entry.scopeName);
            child = dispatchEntry.entry.path.createView(dispatchEntry.scope.createContext(view.getContext()), view);
            if (dispatchEntry.entry.viewState != null) {
                view.restoreHierarchyState(dispatchEntry.entry.viewState);
            }

            view.addView(child);

            if (child instanceof HandlesViewTransition) {
                ((HandlesViewTransition) child).onViewTransition(null);
            }
        }
    }

    /**
     * Present several modals that will all show/hide in parallel
     * Once all view transitions have been executed, the dispatcher callback will complete
     */
    void presentModals(List<Dispatcher.DispatchEntry> modals, final Dispatcher.Callback callback) {
//        Preconditions.checkNotNull(view, "Container view cannot be null");
//        Preconditions.checkArgument(view.hasCurrentView(), "Container view must have current view");
//        Preconditions.checkNull(dispatchingCallback, "Previous dispatching callback not completed");
//
//        Logger.d("Present modals: %d", modals.size());
//
//        // set and track the callback from dispatcher
//        // dispatcher is waiting for the onComplete call
//        // either when present is done, or when presenter is desactivated
//        dispatchingCallback = callback;
//
//        // first, build the views for new modals, or reuse old ones for existing modals
//        final List<NavigatorView.Presentation> presentations = new ArrayList<>(modals.size());
//        Dispatcher.DispatchEntry dispatchEntry;
//        ViewTransition viewTransition;
//        View newView;
//        ViewTransitionDirection direction;
//        for (int i = 0; i < modals.size(); i++) {
//            dispatchEntry = modals.get(i);
//            Logger.d("%s : %s", dispatchEntry.entry.scopeName, dispatchEntry.entry.dead ? "DEAD" : "ALIVE");
//
//            boolean addView;
//            if (dispatchEntry.entry.dead) {
//                newView = view.getChildAt(0);
//                addView = false;
//                direction = ViewTransitionDirection.BACKWARD;
//                Logger.d("Reuse view");
//            } else {
//                newView = dispatchEntry.entry.path.createView(dispatchEntry.scope.createContext(view.getContext()), view);
//                addView = true;
//                direction = ViewTransitionDirection.FORWARD;
//                Logger.d("Create new view");
//            }
//
//            viewTransition = transitions.findTransition(view.getCurrentView(), newView, direction);
//            presentations.add(new NavigatorView.Presentation(newView, addView, !addView, direction, viewTransition));
//        }
//
//        // show/hide them all at once
//        // keep track of view transitions that are finished
//        // and complete dispatching callback one all are done
//        Logger.d("Show presentations: %d", presentations.size());
//        view.showModals(presentations, new PresentationCallback() {
//
//            @Override
//            public void onPresentationFinished(int sessionId) {
//                if (isCurrentSession(sessionId)) {
//                    completeDispatchingCallback();
//                }
//            }
//        });
    }

    void present(final Dispatcher.DispatchEntry enterDispatchEntry, final History.Entry exitEntry, final int direction, final boolean exitEntryInHistory, final Dispatcher.Callback callback) {
        Preconditions.checkNotNull(view, "Container view cannot be null");
        Preconditions.checkNull(dispatchingCallback, "Previous dispatching callback not completed");

        Logger.d("Present new dispatch: %s - with direction: %s", enterDispatchEntry.entry.scopeName, direction);
        Logger.d("Previous entry: %s", exitEntry.scopeName);

        // set and track the callback from dispatcher
        // dispatcher is waiting for the onComplete call
        // either when present is done, or when presenter is desactivated
        dispatchingCallback = callback;

        if (exitEntryInHistory) {
            Logger.d("Save view state for: %s", exitEntry.scopeName);
            exitEntry.viewState = getCurrentViewState();
        }

        // create or reuse view
        View newView;
        boolean addNewView;
        if (view.getChildCount() > 1 && exitEntryInHistory) {
            Logger.d("Reuse previous view for %s", enterDispatchEntry.entry.scopeName);
            newView = view.getChildAt(view.getChildCount() - 2);
            addNewView = false;
        } else {
            Logger.d("Create new view for %s", enterDispatchEntry.entry.scopeName);
            Context context = enterDispatchEntry.scope.createContext(view.getContext());
            newView = enterDispatchEntry.entry.path.createView(context, view);
            addNewView = true;
        }


//        if (!exitEntry.dead || (exitEntry.dead && !exitEntry.isModal())) {
//
////                direction == Dispatcher.Direction.FORWARD ||
////                (direction == Dispatcher.Direction.BACKWARD && !previousEntry.isModal())) {
//            // create new view when forward and replace
//            // or when backward if previous entry is not modal
//            Logger.d("Create new view for %s", enterDispatchEntry.entry.scopeName);
//            Context context = enterDispatchEntry.scope.createContext(view.getContext());
//            newView = enterDispatchEntry.entry.path.createView(context, view);
//            addNewView = true;
//        } else {
//            Logger.d("Reuse previous view for %s", enterDispatchEntry.entry.scopeName);
//            newView = view.getChildAt(view.getChildCount() - 2);
//            addNewView = false;
//        }

        // find transition
        ViewTransition transition;
        if (view.hasCurrentView()) {
            transition = transitions.findTransition(view.getCurrentView(), newView, direction);
        } else {
            transition = null;
        }

        // restore state if it exists
        if (enterDispatchEntry.entry.viewState != null) {
            Logger.d("Restore view state for: %s", enterDispatchEntry.entry.scopeName);
            newView.restoreHierarchyState(enterDispatchEntry.entry.viewState);
        }

//        if (enterDispatchEntry.entry.receivedResult != null) {
//            if (newView instanceof HasPresenter) {
//                // put result
//                ViewPresenter viewPresenter = ((HasPresenter) newView).getPresenter();
//                if (viewPresenter instanceof ReceivesResult) {
//                    ((ReceivesResult) viewPresenter).onReceivedResult(enterDispatchEntry.entry.receivedResult);
//                }
//            }
//            enterDispatchEntry.entry.receivedResult = null;
//        }

//        boolean keepPreviousView = direction == Dispatcher.Direction.FORWARD && newDispatch.entry.isModal();
        boolean keepPreviousView = !exitEntryInHistory && enterDispatchEntry.entry.isModal();
        Logger.d("Keep previous view: %b", keepPreviousView);

        view.show(new NavigatorView.Presentation(newView, addNewView, !keepPreviousView, direction, transition), new PresentationCallback() {
            @Override
            public void onPresentationFinished(int sessionId) {
                if (isCurrentSession(sessionId)) {
                    completeDispatchingCallback();
                }
            }
        });
    }

    MortarScope getCurrentScope() {
        return view.hasCurrentView() ? MortarScope.getScope(view.getCurrentView().getContext()) : null;
    }

    boolean containerViewOnBackPressed() {
        return view != null && view.onBackPressed();
    }

    private SparseArray<Parcelable> getCurrentViewState() {
        Preconditions.checkNotNull(view, "Container view cannot be null");
        Preconditions.checkArgument(view.hasCurrentView(), "Save view state requires current view");

        View view = this.view.getCurrentView();
        SparseArray<Parcelable> state = new SparseArray<>();
        view.saveHierarchyState(state);
        return state;
    }

    boolean isActive() {
        return view != null && active;
    }

    boolean isCurrentSession(int sessionId) {
        return this.sessionId == sessionId;
    }

    private void completeDispatchingCallback() {
        if (dispatchingCallback == null) {
            return;
        }

        Dispatcher.Callback callback = dispatchingCallback;
        dispatchingCallback = null;  // onComplete redispatches direclty so we need to remove ref now
        callback.onComplete();
    }

    interface PresentationCallback {

        void onPresentationFinished(int sessionId);
    }
}