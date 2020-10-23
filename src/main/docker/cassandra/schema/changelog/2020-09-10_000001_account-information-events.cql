CREATE TABLE IF NOT EXISTS account_information (
    aggregateId uuid,
    majorVersion int,
    minorVersion int,
    event text,

    PRIMARY KEY(aggregateId, majorVersion, minorVersion)
);