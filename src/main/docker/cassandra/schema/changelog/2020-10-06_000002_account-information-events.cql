CREATE TABLE IF NOT EXISTS account_information_2 (
    aggregateId uuid,
    majorVersion int,
    minorVersion int,
    eventId timeuuid,
    event text,

    PRIMARY KEY(aggregateId, majorVersion, minorVersion, eventId)
);