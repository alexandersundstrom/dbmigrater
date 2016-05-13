CREATE VIEW event_view AS
    SELECT 
        event.oid AS event_oid,
        event.title AS event_title,
        event.abstract AS event_abstract,
        event.note AS event_note,
        event.conference_oid AS event_conference_id,
        event.location_oid AS event_location,
        event.track_oid AS event_track_oid,
        event.slidesUrl AS event_slideURL,
        event.videourl AS event_videoURL,
        event.propertyName AS event_propertyName,
        event.lastModified AS event_last_modified,
        event.shortTitle AS event_shortTitle,
        event_slot.slot_id AS event_slot_slot_id,
        event_slot.event_id AS event_slot_event_id,
        event_slot.lastModified AS event_slot_last_modified,
        track.oid AS track_oid,
        track.name AS track_name,
        track.description AS track_description,
        track.color AS track_color,
        track.conference_oid AS track_conference_id,
        track.shortDescription AS track_shortDescription,
        track.host AS track_host,
        track.lastModified AS track_last_modified,
        location.oid AS location_oid,
        location.name AS location_name,
        location.description AS location_description,
        location.conference_oid AS location_conference_oid,
        location.lastModified AS location_lastModified
    FROM
        event
            LEFT JOIN
        track ON track.oid = event.track_oid
            LEFT JOIN
        event_slot ON event_slot.event_id = event.oid
            LEFT JOIN
        location ON location.oid = event.location_oid;