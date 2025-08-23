package at.wls_android.app.enums

@kotlinx.serialization.Serializable
enum class DisturbanceType(val text: String) {
    Delay("Verspätungen"),
    Accident("Unfälle"),
    AmbulanceOperation("Rettungseinsätze"),
    FireDepartmentOperation("Feuerwehreinsätze"),
    PoliceOperation("Polizeieinsatz"),
    ParkingOffender("Falschparker"),
    DefectiveVehicle("Schadhafte Fahrzeug"),
    CatenaryDamage("Fahrleitungsschäden"),
    TrackDamage("Gleisschäden"),
    SignalDamage("Signalstörungen"),
    SwitchDamage("Weichenstörungen"),
    ConstructionWork("Bauarbeiten"),
    Demonstration("Demonstrationen"),
    Event("Veranstaltungen"),
    Weather("Witterungsbedingt"),
    Misc("Sonstiges"),
}
