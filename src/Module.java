class Module {
    private boolean started;
    private String name;

    Module(String moduleName, boolean status){
        name = moduleName;
        started = status;
    }

    boolean isStarted() {
        return started;
    }

    String getName() {
        return name;
    }
}
